package main;

import delegate.Delegate;
import model.Model;

public class Main {
    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        Model model = new Model();
        boolean initWithDarkTheme = false;
        if (args.length == 1) {
            if (args[0].equals("dark"))
                initWithDarkTheme = true;
        }
        Delegate delegate = new Delegate(model, initWithDarkTheme);
    }
}
