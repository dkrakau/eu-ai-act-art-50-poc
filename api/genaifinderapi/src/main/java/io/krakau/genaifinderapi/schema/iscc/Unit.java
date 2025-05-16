package io.krakau.genaifinderapi.schema.iscc;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author Dominik
 */
public class Unit {
    
    @Schema(type = "string", example = "META-NONE-V0-64-c8a70639eb1167b3", description = "readable")
    private String readable;
    
    @Schema(type = "string", example = "e1fb7dc4e3dbb4be", description = "hash_hex")
    private String hash_hex;
    
    @Schema(type = "string", example = "ISCC:AAA4RJYGHHVRCZ5T", description = "iscc_unit")
    private String iscc_unit;
    
    @Schema(type = "string", example = "1110000111111011011111011100010011100011110110111011010010111110", description = "hash_bits")
    private String hash_bits;
    
    @Schema(type = "string", example = "16283747162278048958", description = "hash_unit")
    private String hash_unit;

    public Unit(String readable, String hash_hex, String iscc_unit, String hash_bits, String hash_unit) {
        this.readable = readable;
        this.hash_hex = hash_hex;
        this.iscc_unit = iscc_unit;
        this.hash_bits = hash_bits;
        this.hash_unit = hash_unit;
    }

    public void setReadable(String readable) {
        this.readable = readable;
    }

    public void setHash_hex(String hash_hex) {
        this.hash_hex = hash_hex;
    }

    public void setIscc_unit(String iscc_unit) {
        this.iscc_unit = iscc_unit;
    }

    public void setHash_bits(String hash_bits) {
        this.hash_bits = hash_bits;
    }

    public void setHash_unit(String hash_unit) {
        this.hash_unit = hash_unit;
    }

    public String getReadable() {
        return readable;
    }

    public String getHash_hex() {
        return hash_hex;
    }

    public String getIscc_unit() {
        return iscc_unit;
    }

    public String getHash_bits() {
        return hash_bits;
    }

    public String getHash_unit() {
        return hash_unit;
    }

    @Override
    public String toString() {
        return "Unit{" + "readable=" + readable + ", hash_hex=" + hash_hex + ", iscc_unit=" + iscc_unit + ", hash_bits=" + hash_bits + ", hash_unit=" + hash_unit + '}';
    }
    
}