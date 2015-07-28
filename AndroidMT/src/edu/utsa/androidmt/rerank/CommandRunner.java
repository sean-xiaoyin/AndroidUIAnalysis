package edu.utsa.androidmt.rerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandRunner {
    public static CommandResult runCommand(String command) throws IOException,
	    InterruptedException {
	Process p = Runtime.getRuntime().exec(command);
	return CommandRunner.getResult(p);
    }

    public static CommandResult runCommand(String command, File workDir)
	    throws IOException, InterruptedException {
//	String[] path = new String[1];
//	path[0] = "path=" + System.getenv("path");
	Process p = Runtime.getRuntime().exec(command, null, workDir);
	return CommandRunner.getResult(p);
    }

    public static CommandResult runCommand(String[] commands, File workDir)
	    throws IOException, InterruptedException {
	Process p = Runtime.getRuntime().exec(commands, null, workDir);
	return CommandRunner.getResult(p);
    }

    public static CommandResult getResult(Process p) throws IOException,
	    InterruptedException {
	BufferedReader stdInput = new BufferedReader(new InputStreamReader(
		p.getInputStream()));
	BufferedReader stdError = new BufferedReader(new InputStreamReader(
		p.getErrorStream()));

	String s;
	String stdOut = "";
	while ((s = stdInput.readLine()) != null) {
	    stdOut = stdOut + "\n" + s;
	}
	String stdErr = "";
	while ((s = stdError.readLine()) != null) {
	    stdErr = stdErr + "\n" + s;
	}
	int val = p.waitFor();
	stdInput.close();
	stdError.close();
	return new CommandResult(stdOut, stdErr, val);
    }

    public static class CommandResult {
	private String stdOutput;
	private int returnFlag;
	private String errOutput;

	public CommandResult(String stdOut, String errOut, int retval) {
	    this.stdOutput = stdOut;
	    this.errOutput = errOut;
	    this.returnFlag = retval;
	}

	public String getStdOut() {
	    return this.stdOutput;
	}

	public String getErrOut() {
	    return this.errOutput;
	}

	public int getReturnFlag() {
	    return this.returnFlag;
	}

	public String toString() {
	    return this.returnFlag + "\nStdOutput:\n" + this.stdOutput
		    + "\nStdErr:\n" + this.errOutput;
	}

	public String getSimpleReport() {
	    if (this.returnFlag != 0) {
		return this.errOutput;
	    }
	    return "";
	}
    }
}
