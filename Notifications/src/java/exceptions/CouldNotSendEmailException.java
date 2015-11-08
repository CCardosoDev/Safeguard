/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author claudia
 */
public class CouldNotSendEmailException extends Exception{

    public CouldNotSendEmailException(String message) {
        super(message);
    }
    
}
