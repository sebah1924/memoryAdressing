/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.arqui;

/**
 *
 * @author estudiante
 */
public class RealMode {
    
    
    public int calculateRealMode(int segment, int offset){
        int part1=segment*16;
        return part1+offset;
    }
    
    
    
}
