/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.optimizer;

import ibd.query.Operation;

/**
 *
 * @author ferna
 */
public interface QueryOptimizer {
    public Operation optimize(Operation op);
    
}
