package com.alibaba.alink.common.pyrunner;

import org.apache.flink.util.Preconditions;

import com.alibaba.alink.common.AlinkGlobalConfiguration;
import com.alibaba.alink.common.dl.DLEnvConfig;
import com.alibaba.alink.common.dl.DLEnvConfig.Version;
import com.alibaba.alink.common.dl.utils.ArchivesUtils;
import com.alibaba.alink.common.dl.utils.PythonFileUtils;
import com.alibaba.alink.common.io.filesystem.FilePath;
import com.alibaba.alink.common.io.plugin.RegisterKey;
import com.alibaba.alink.common.io.plugin.ResourcePluginFactory;
import com.alibaba.alink.common.pyrunner.bridge.BasePythonBridge;
import com.alibaba.alink.common.pyrunner.bridge.DedicatedPythonBridge;
import com.alibaba.alink.common.utils.Functional.SerializableBiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A runner which calls Python code to do calculation.
 *
 * @param <IN>     input data type
 * @param <OUT>    output data type
 * @param <HANDLE> Python object handle type
 */
public abstract class PyCalcRunner<IN, OUT, HANDLE extends PyObjHandle> {

	private static final Logger LOG = LoggerFactory.getLogger(PyCalcRunner.class);

	protected final SerializableBiFunction <String, String, String> getConfigFn;

	// Use Python env from plugin directory.
	// PyScalar/TableFnRunner should not use plugin, as they are using cloudpickle for (de)serialization which requires
	// the same Python version.
	private final boolean usePluginPythonEnv;
	private final ResourcePluginFactory factory;

	private final String pythonClassName;
	private final BasePythonBridge bridge = DedicatedPythonBridge.inst();

	protected HANDLE handle;

	/**
	 * Construct a runner using system python env.
	 */
	public PyCalcRunner(String pythonClassName, SerializableBiFunction <String, String, String> getConfigFn) {
		this(pythonClassName, getConfigFn, false, null);
	}

	/**
	 * Construct a runner using plugin python env.
	 */
	public PyCalcRunner(String pythonClassName, SerializableBiFunction <String, String, String> getConfigFn,
						ResourcePluginFactory factory) {
		this(pythonClassName, getConfigFn, true, factory);
	}

	private PyCalcRunner(String pythonClassName, SerializableBiFunction <String, String, String> getConfigFn,
						 boolean usePluginPythonEnv, ResourcePluginFactory factory) {
		this.pythonClassName = pythonClassName;
		this.getConfigFn = getConfigFn;
		this.usePluginPythonEnv = usePluginPythonEnv;
		this.factory = factory;
	}

	/**
	 * Start Python process if necessary and create the handle.
	 */
	public void open() {
		String pythonEnv = getConfigFn.apply(BasePythonBridge.PY_VIRTUAL_ENV_KEY, null);
		if (null != pythonEnv) {
			if (PythonFileUtils.isCompressedFile(pythonEnv)) {
				String tempWorkDir = PythonFileUtils.createTempDir("python_env_").toString();
				ArchivesUtils.downloadDecompressToDirectory(pythonEnv, new File(tempWorkDir));
				pythonEnv = new File(tempWorkDir, PythonFileUtils.getCompressedFileName(pythonEnv)).getAbsolutePath();
			} else {
				if (PythonFileUtils.isLocalFile(pythonEnv)) {
					pythonEnv = pythonEnv.substring("file://".length());
				}
			}
		} else if (usePluginPythonEnv) {
			FilePath pluginFilePath = null;
			RegisterKey tf1RegisterKey = DLEnvConfig.getRegisterKey(Version.TF115);
			RegisterKey tf2RegisterKey = DLEnvConfig.getRegisterKey(Version.TF231);
			try {
				pluginFilePath = factory.getResourcePluginPath(tf1RegisterKey, tf2RegisterKey);
			} catch (Exception e) {
				String info = String.format("Cannot prepare plugin for %s-%s, and %s-%s, fallback to use system Python.",
					tf1RegisterKey.getName(), tf1RegisterKey.getVersion(),
					tf2RegisterKey.getName(), tf2RegisterKey.getVersion());
				LOG.info(info, e);
				if (AlinkGlobalConfiguration.isPrintProcessInfo()) {
					System.out.println(info + ": " + e);
				}
			}
			if (null != pluginFilePath) {
				File pluginDirectory = new File(pluginFilePath.getPath().getPath());
				File[] dirs = pluginDirectory.listFiles(File::isDirectory);
				Preconditions.checkArgument(null != dirs && dirs.length == 1,
					String.format("There should be only 1 directory in plugin directory: %s.", pluginDirectory));
				pythonEnv = dirs[0].getAbsolutePath();
			}
		}

		String finalPythonEnv = pythonEnv;
		LOG.info("Use virtual env in {}", pythonEnv);
		if (AlinkGlobalConfiguration.isPrintProcessInfo()) {
			System.out.println("Use virtual env in " + pythonEnv);
		}
		SerializableBiFunction <String, String, String> newGetConfigFn = (key, defaultValue) ->
			BasePythonBridge.PY_VIRTUAL_ENV_KEY.equals(key)
				? finalPythonEnv
				: getConfigFn.apply(key, defaultValue);

		bridge.open(getClass().getName(), newGetConfigFn, null);
		this.handle = bridge.app().newobj(pythonClassName);
	}

	public abstract OUT calc(IN in);

	/**
	 * Destroy the handle and stop Python process when necessary.
	 */
	public void close() {
		handle = null;
		bridge.close(this.getClass().getName());
	}
}
