package main;

import delegate.Delegate;
import model.Model;

public class Main {
    public static void main(String[] args) throws Exception{
    	Model model = new Model();
        Delegate delegate = new Delegate(model);
    }
}
