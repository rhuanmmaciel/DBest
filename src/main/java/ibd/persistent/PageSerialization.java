/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.persistent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Sergio
 */
public interface PageSerialization {
    
    void writePage(DataOutputStream oos, Page page) throws IOException;
    Page readPage(DataInputStream ois) throws IOException;
    
}
