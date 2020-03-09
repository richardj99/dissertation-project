package Politika;

import java.util.ArrayList;

public class NetworkOutputStruct {
    Double[] nnOutput;
    ArrayList<String> encodedText;

    public NetworkOutputStruct(Double[] i_nnOutput, ArrayList<String> i_encodedText){
        nnOutput = i_nnOutput;
        encodedText = i_encodedText;
    }
}
