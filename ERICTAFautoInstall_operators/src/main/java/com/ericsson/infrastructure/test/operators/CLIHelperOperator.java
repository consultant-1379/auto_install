package com.ericsson.infrastructure.test.operators;

import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.google.inject.Singleton;

/**
 * Simple operator class around CLIHelper
 *
 */
@Operator(context = Context.CLI)
@Singleton
public class CLIHelperOperator {    

    Logger logger = LoggerFactory.getLogger(CLIHelperOperator.class);
    
    private static String JSCH_EXIT_CODE_MARKER = "taf.jsch.exitcode";
    private static String ECHO_EXIT_CODE_CMD = "echo \"" + JSCH_EXIT_CODE_MARKER + "=$?\"";

    
    private CLICommandHelper cliCommandHelper;
    
    private Map<String, CLICommandHelper> helperList = new HashMap<String, CLICommandHelper>();

    /**
     * Initialises cliCommandHelper and opens shell
     * @param host
     * @param user
     */
    public void initializeHelper(final Host host, final User user) {
        logger.debug("Opening shell to " + host.getHostname() + " with user " + user.getUsername());
        setHelper(host, user);
        cliCommandHelper.openShell();
    }   
    
    /**
     * Returns whether the shell is closed
     * @return if shell is closed
     */
    public boolean isShellClosed() {
        return cliCommandHelper.isClosed();
    }
    
    /**
     * Executes command on CLI. Do not use for long-running commands
     * @param command
     * @return stdOut from command.
     */
    public String execute(final String command) {
        logger.debug("Executing command: " + command);
        return cliCommandHelper.execute(command);
    }
    
    /**
     * Executes command on CLI and disconnects. Do not use for long-running commands.
     * @param command
     * @return stdOut from command.
     */
    public String execute(final Host host, final User user, final String command) {
        
        logger.debug("Initialising with " + host.getHostname() + " with user " + user.getUsername());
        setHelper(host, user);
        logger.debug("Simple Executing command: " + command);
        String output = null;
        try {
            logger.info("We are in execute method and running the execute");
            output = cliCommandHelper.execute(command);
        } finally {
            cliCommandHelper.disconnect();            
        }
        logger.debug("Checking connection is closed");
        if (!cliCommandHelper.isClosed()) {
            logger.error("Command helper not closed");
        }
        //cliCommandHelper=null;
        return output;
    }    
    /**
     * Executes command on CLI and disconnects. Do not use for long-running commands.
     * @param command
     * @return stdOut from command.
     */
    public String simpleExec(final Host host, final User user, final String command) {
    	
        logger.debug("Initialing with " + host.getHostname() + " with user " + user.getUsername());
        setHelper(host, user);
        logger.debug("Simple Executing command: " + command);
        String output = null;
        try {
        	logger.info("We are in simpleExec method and running the simpleExec");
            output = cliCommandHelper.simpleExec(command);
        } finally {
            cliCommandHelper.disconnect();            
        }
        logger.debug("Checking connection is closed");
        if (!cliCommandHelper.isClosed()) {
            logger.error("Command helper not closed");
        }
        //cliCommandHelper=null;
        return output;
    }    
   
    /**
      * Creates a new CLICommandHelper if one doesn't already exist for this 
      * host/user
      */ 
    private void setHelper(final Host host, final User user) {
        cliCommandHelper = helperList.get(host.getHostname() + "/" + user.getUsername());
        logger.info("We are in cliCommandHelper method");
        if (cliCommandHelper != null) {
        	cliCommandHelper.disconnect();
        	logger.info("We are making clicommandHelper instance to null for creating new Instance");
        	cliCommandHelper=null;
        	
        }
        
        if (cliCommandHelper == null) {
            logger.debug("Creating new helper for " + host.getHostname() + "/" + user.getUsername());
            cliCommandHelper = new CLICommandHelper(host, user);
            helperList.put(host.getHostname() + "/" + user.getUsername(), cliCommandHelper);
        } else {
            logger.debug("Using existing helper for " + host.getHostname() + "/" + user.getUsername());
            logger.debug("Disconnect in case shell is still open");
            cliCommandHelper.disconnect();
          //  cliCommandHelper=null;
        }
    }
    
    /**
     * Runs an interactive script
     * @param command - script to run
     */
    public void runInteractiveScript(final String command) {
        logger.debug("Executing interactive command: " + command);
        cliCommandHelper.runInteractiveScript(command);
    }
    
    /**
     * Runs an interactive script with a marker so can wait for end. 
     * By using marker, can use getCommandExitValue to find out exit codes
     * @param command - script to run
     */
    public void runInteractiveScriptAndStop(final String command) {
        logger.debug("Executing interactive command: " + command);
        cliCommandHelper.runInteractiveScript(command + "; " + ECHO_EXIT_CODE_CMD);
    }   
    
    /**
     * Waits for the end marker when running an interactive script that was invoked using runInteractiveScriptAndStop
     * @return String - matched code
     */
    public String waitForInteractiveToEnd() {
        return expect(Pattern.compile(JSCH_EXIT_CODE_MARKER + "=\\d+?"));
    }
    
    /**
     * Waits for the end marker when running an interactive script that was invoked using runInteractiveScriptAndStop
     * @return String - matched code
     */
    public String waitForInteractiveToEnd(long timeout) {
        return expect(Pattern.compile(JSCH_EXIT_CODE_MARKER + "=\\d+?"), timeout);
    }
    
    /**
     * Returns the stdout of last command
     * @return String 
     */
    public String getStdOut() {
        return cliCommandHelper.getStdOut();
    }
    
    /**
     * When running interactive command, match the expected string
     * @param expect
     * @return String
     */
    public String expect(final String expect) {
        logger.debug("Expecting: " + expect);
        String received = cliCommandHelper.expect(expect);
        logger.debug("Matched: " + received);
        return received;
    }
    
    /**
     * When running interactive command, match the expected string
     * @param expect
     * @return String
     */
    public String expect(final Pattern expect, long timeout) {
        logger.debug("Expecting: " + expect);
        String received = cliCommandHelper.expect(expect, timeout);
        logger.debug("Matched: " + received);
        return received;
    }    
    
    /**
     * When running interactive command, match the expected string
     * @param expect
     * @return String
     */
    public String expect(final Pattern expect) {
        logger.debug("Expecting: " + expect);
        String received = cliCommandHelper.expect(expect);
        logger.debug("Matched: " + received);
        return received;
    }       
    
    /**
     * When running interactive command, match the expected string
     * @param expect
     * @param timeout - timeout in seconds
     * @return
     */
    public String expect(final String expect, long timeout) {
        logger.debug("Expecting: " + expect);
        String received = cliCommandHelper.expect(expect, timeout);
        logger.debug("Matched: " + received);
        return received;
    }
    
    /**
     * Interact with the shell
     * @param input
     */
    public void interactWithShell(final String input) {
        logger.debug("Interacting with: " + input);
        cliCommandHelper.interactWithShell(input);

    }
    
    /**
     * Interact with shell, waiting for question, and responding with answer
     * @param question - String to match on
     * @param answer - Input to give to shell when matched question
     * @return true/false
     */
    public Boolean interactWithShell(final String question, final String answer) {
        logger.debug("Expecting: " + question + " and answer " + answer);
        try 
        {
        	expect(question);
            interactWithShell(answer);
            return true;        	
        } catch(Exception e){
        
        	logger.debug("Expected question didnt occur,verify your question :"+question +",err:"+e.getMessage());
            return false;

        }
        
    }
    
    /**
     * Interact with shell, waiting for question, and responding with answer
     * @param question - String to match on
     * @param answer - Input to give to shell when matched question
     * @return true/false
     */
    public String interactWithShell(final String question, final String answer, long timeout) {
        logger.debug("Expecting: " + question + " and answer " + answer);
        String matched = null;
        try 
        {
            matched = expect(question, timeout);
            interactWithShell(answer);
        } catch(Exception e){
        
            logger.error("Expected question didnt occur,verify your question :"+question +",err:"+e.getMessage());

        }
        return matched;
        
    }
    
    /**
     * Interact with shell, waiting for question, responding with answer and return boolean true/false
     * @param timeout - timeout in seconds
     * @param question - String to match on
     * @param answer - Input to give to shell when matched question
     * @return true/false(boolean)
     */
    public boolean interactWithShell(long timeout, final String question, final String answer) {
        logger.debug("Expecting: " + question + " and answer " + answer);  
        try 
        {
            expect(question, timeout);
            interactWithShell(answer);
            return true;
        } catch (Exception e) {
        
            logger.error("Expected question didnt occur,verify your question :"+question +",err:"+e.getMessage());
            return false;

        }
    }
    
    /**
     * Expect shell to close within default timeout
     */
    public void expectShellClosure() {
        logger.debug("Expecting shell closure");
        cliCommandHelper.expectShellClosure();
    }
    
    /**
     * Expect shell to close within specified timeout
     * @param timeoutInSeconds
     */
    public void expectShellClosure(final long timeoutInSeconds) {
        logger.debug("Expecting shell closure");
        cliCommandHelper.expectShellClosure(timeoutInSeconds);
    }

    /**
     * Return the exit value from last command
     * @return exitCode from running command (via execute)
     */
    public int getCommandExitValue() {
        return cliCommandHelper.getCommandExitValue();
    }
    
    /**
     * Closes shell
     */
    public void closeShell() {
        logger.debug("Close and validate shell");
        cliCommandHelper.closeAndValidateShell();
    }
    
    /**
     * Return the exit code from the shell
     * @return int
     */
    public int getShellExitValue() {
        return cliCommandHelper.getShellExitValue();
    }

}

