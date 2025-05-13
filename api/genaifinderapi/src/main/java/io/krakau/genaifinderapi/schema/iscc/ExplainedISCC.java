package io.krakau.genaifinderapi.schema.iscc;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 *
 * @author Dominik
 */
@Schema
public class ExplainedISCC {

    @Schema(type = "string", example = "ISCC:KAC63PQ2AXAVCNXKN7UD5QS2TGJ3M3S6ZTTZJAR7H2PC6DWZ2AJLDKA", description = "ISCC code")
    private String iscc;
    
    @Schema(type = "string", example = "ISCC-TEXT-V0-MCDI-edbe1a05c15136ea6fe83ec25a9993b66e5ecce794823f3e9e2f0ed9d012b1a8", description = "readable")
    private String readable;
    
    @Schema(type = "string", example = "uzAFQBe2-GgXBUTbqb-g-wlqZk7ZuXsznlII_Pp4vDtnQErGo", description = "multiformat")
    private String multiformat;
    
    @Schema(type = "string", example = "AAA63PQ2AXAVCNXK-EAAW72B6YJNJTE5W-GAAW4XWM46KIEPZ6-IAAZ4LYO3HIBFMNI", description = "decomposed")
    private String decomposed;
    
    @ArraySchema( arraySchema =  @Schema(
	description = "Units of ISCC",
	example = "[\n" +
            "{\n" +
                "\"readable\": \"META-NONE-V0-64-edbe1a05c15136ea\",\n" +
                "\"hash_hex\": \"edbe1a05c15136ea\",\n" +
                "\"iscc_unit\": \"ISCC:AAA63PQ2AXAVCNXK\",\n" +
                "\"hash_bits\": \"1110110110111110000110100000010111000001010100010011011011101010\",\n" +
                "\"hash_unit\": \"17131158644584429290\"\n" +
            "},\n" +
            "{\n" +
                "\"readable\": \"CONTENT-TEXT-V0-64-6fe83ec25a9993b6\",\n" +
                "\"hash_hex\": \"6fe83ec25a9993b6\",\n" +
                "\"iscc_unit\": \"ISCC:EAAW72B6YJNJTE5W\",\n" +
                "\"hash_bits\": \"0110111111101000001111101100001001011010100110011001001110110110\",\n" +
                "\"hash_unit\": \"8063764137271464886\"\n" +
            "},\n" +
            "{\n" +
                "\"readable\": \"DATA-NONE-V0-64-6e5ecce794823f3e\",\n" +
                "\"hash_hex\": \"6e5ecce794823f3e\",\n" +
                "\"iscc_unit\": \"ISCC:GAAW4XWM46KIEPZ6\",\n" +
                "\"hash_bits\": \"0110111001011110110011001110011110010100100000100011111100111110\",\n" +
                "\"hash_unit\": \"7953019286983950142\"\n" +
            "},\n" +
            "{\n" +
                "\"readable\": \"INSTANCE-NONE-V0-64-9e2f0ed9d012b1a8\",\n" +
                "\"hash_hex\": \"9e2f0ed9d012b1a8\",\n" +
                "\"iscc_unit\": \"ISCC:IAAZ4LYO3HIBFMNI\",\n" +
                "\"hash_bits\": \"1001111000101111000011101101100111010000000100101011000110101000\",\n" +
                "\"hash_unit\": \"11398345510559592872\"\n" +
            "}\n" +
        "]"
    ))
    private List<Unit> units;

    public ExplainedISCC(String iscc, String readable, String multiformat, String decomposed, List<Unit> units) {
        this.iscc = iscc;
        this.readable = readable;
        this.multiformat = multiformat;
        this.decomposed = decomposed;
        this.units = units;
    }

    public void setIscc(String iscc) {
        this.iscc = iscc;
    }

    public void setReadable(String readable) {
        this.readable = readable;
    }

    public void setMultiformat(String multiformat) {
        this.multiformat = multiformat;
    }

    public void setDecomposed(String decomposed) {
        this.decomposed = decomposed;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public String getIscc() {
        return iscc;
    }

    public String getReadable() {
        return readable;
    }

    public String getMultiformat() {
        return multiformat;
    }

    public String getDecomposed() {
        return decomposed;
    }

    public List<Unit> getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return "ExplainedISCC{" + "iscc=" + iscc + ", readable=" + readable + ", multiformat=" + multiformat + ", decomposed=" + decomposed + ", units=" + units + '}';
    }
    
}