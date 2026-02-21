/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public abstract class Element {
    public abstract Comparable getValue(Tuple tuple);
    public abstract Comparable getValueFromRow(LinkedDataRow row);
}
