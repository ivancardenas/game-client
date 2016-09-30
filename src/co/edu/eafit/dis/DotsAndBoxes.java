
package co.edu.eafit.dis;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.ui.GameLoginUI;

public class DotsAndBoxes {
    
    public static void main(String[] args) {
        
        if (args.length == 0)
            API.setURL("http://10.131.137.164:3000/");
        else API.setURL(args[0]); // Receive args.
        
        new GameLoginUI().setVisible(true);
    }
}