package com.ericsson.infrastructure.test.operators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.google.inject.Singleton;

/**
 * Operator class that wraps CliCommandHelper and CLIOperator
 *
 */
@Operator(context = Context.CLI)
@Singleton
public class CLIOperator extends CLIHelperOperator{    

    Logger logger = LoggerFactory.getLogger(CLIOperator.class);

    private CLI cli;
    
    private Map<String, CLI> helperList = new HashMap<String, CLI>();
  
    /**
     * Initialise CLI intstance
     * @param host
     * @param user
     */
    public void initialize(final Host host, final User user) {
        cli = helperList.get(host.getHostname() + "/" + user.getUsername());
        logger.info("We are in CLIOperator initialize method");
        if (cli != null) {
        	logger.info("We are in CLIOperator initialize method and cli is not null so making null for creating a new instance");
        	cli=null;
        }
        
        if (cli == null) {
            logger.debug("Creating new CLI for " + host.getHostname() + "/" + user.getUsername());
            cli = new CLI(host, user);
            helperList.put(host.getHostname() + "/" + user.getUsername(), cli);
        } else {
            logger.debug("Using existing CLI for " + host.getHostname() + "/" + user.getUsername());
        }     
       
    } 
    
    /**
     * Executes command on CLI, returns Shell that is running command
     * @param command
     * @return Shell
     */
    public Shell executeCommand(final String command){
    	logger.info("We are in execute Command method");
        logger.debug("Running command: " + command);
        return cli.executeCommand(command);
    }
   
    
    /**
     * Read the stdout from shell that is executing a command (via executeCommand), and
     * optionally display it
     * @param shell
     * @param displayOut
     * @return The stdout
     */
    public String readUntilClosed(final Shell shell, final boolean displayOut) {
        boolean wasNewLine = false;
        String stdOut = shell.read();
        wasNewLine = displayLine(stdOut, wasNewLine, displayOut);
        logger.info("We are in readUntilClosed method");
        String output;
        while(!shell.isClosed()){
            output=shell.read();
            wasNewLine = displayLine(output, wasNewLine, displayOut);
            stdOut = stdOut + output;
        }
        output = shell.read();
        displayLine(output, wasNewLine, displayOut);
        stdOut = stdOut + output;
        return stdOut;
    }
    
    /**
     * Output read line to screen
     * @param output - line read
     * @param wasNewLine - whether already found empty new line
     * @param displayOut - whether to displayoutput to screen
     * @return - whether this is empty new line
     */
    private boolean displayLine(String output, boolean wasNewLine, boolean displayOut) {
        // Don't output multiple blank lines
        if ((output == null) || (output.length() == 0)) {
            if ( ! wasNewLine) {
                // Last line wasn't new line so output
                if (displayOut) {
                    logger.info(output, true);
                    wasNewLine = true;
                }
            }
        } else {
            wasNewLine = false;
            if (displayOut) {
              logger.info(output, true);
            }
        }
        return wasNewLine;
    }

    /**
     * Returns the exit code from the shell (after running executeCommand)
     * @param shell
     * @return int 
     */
    public int getShellExitValue(final Shell shell) {
        int cmdExitValue = shell.getExitValue();
        logger.debug("Shell returned " + cmdExitValue);
        return cmdExitValue;
    }  
    
    /**
     * Sends file to remote server. Searches workspace for file that matches.
     * @param host
     * @param user
     * @param fileName
     * @param fileServerLocation
     * @return boolean
     */
    public boolean sendFileRemotely(final Host host, final User user, 
            final String fileName,
            final String fileServerLocation)  {

        RemoteFileHandler remote = new RemoteFileHandler(host, user);
        logger.debug("Copying " + fileName + " to " + fileServerLocation
                + " on remote host");
        boolean sent = remote.copyLocalFileToRemote(fileName, fileServerLocation);
        logger.debug("Copy succeeded? : " + sent);
        return sent;

    }
    
    
   // com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler
    public boolean sendFileRemotely2(final Host host, final User user, 
            final String fileName,
            final String fileServerLocation)  {

    	RemoteObjectHandler remote = new RemoteObjectHandler(host, user);
        logger.debug("Copying " + fileName + " to " + fileServerLocation
                + " on remote host");
        boolean sent = remote.copyLocalFileToRemote(fileName, fileServerLocation);
        logger.debug("Copy succeeded? : " + sent);
        return sent;

    }
    /**
     * Gets file from remote server. 
     * @param host
     * @param user
     * @param remoteFile
     * @param localFile
     * @return boolean
     */
    public boolean getRemoteFile(final Host host, final User user, 
            final String remoteFile,
            final String localFile)  {

         
        if (!checkRemoteFileExists(host, user, remoteFile)) {
            return false;
        }
        
        File lFile = new File(localFile);
        RemoteFileHandler remote = new RemoteFileHandler(host, user);
       

        logger.debug("Copying " + remoteFile + " to " + lFile.getAbsolutePath()
                + " from remote host " + host.getHostname());
         
        boolean copied = remote.copyRemoteFileToLocal(remoteFile, lFile.getAbsolutePath());
        return copied;

    }
    
    
    /**
     * Checks file is on remote server. 
     * @param host
     * @param user
     * @param remoteFile
     * @return boolean
     */
    public boolean checkRemoteFileExists(final Host host, final User user, 
            final String remoteFile)  {

        RemoteFileHandler remote = new RemoteFileHandler(host, user);
        
        if (!remote.remoteFileExists(remoteFile)) {
            logger.debug("Remote file does not exist");
            return false;
        }

        return true;

    }  

    /**
     * Deletes files on remote server
     * @param host
     * @param user
     * @param fileName - full path to file on remote server
     */
    public boolean deleteRemoteFile(final Host host, final User user, final String fileName) {

        RemoteFileHandler remoteFileHandler = new RemoteFileHandler(host, user);
        logger.debug("deleting " + fileName + " on remote host");
        boolean deleted = remoteFileHandler.deleteRemoteFile(fileName);
        logger.debug("deletion successful: "+ deleted);
        return deleted;
    }
    
    /**
     * Disconnects the shell
     * @param shell
     */
    public void disconnect(Shell shell) {

        logger.debug("Disconnecting from shell");
        shell.disconnect();
        shell = null;
    }
}

