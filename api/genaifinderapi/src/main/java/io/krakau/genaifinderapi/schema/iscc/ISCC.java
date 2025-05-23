package io.krakau.genaifinderapi.schema.iscc;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import javax.validation.constraints.Size;
import org.json.JSONObject;

/**
 *
 * @author Dominik
 */
@Schema
public class ISCC {

    @JsonProperty("@context")
    private String context;

    @JsonProperty("@type")
    @NotBlank(message = "Type is required")
    private String type;

    @JsonProperty("$schema")
    private String schema;

    @JsonProperty("iscc")
    @NotBlank(message = "ISCC is required")
    private String iscc;

    @JsonProperty("content")
    @NotBlank(message = "Content URL is required")
    private String content;

    @JsonProperty("media_id")
    @NotBlank(message = "Media ID is required")
    private String mediaId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("mode")
    @NotBlank(message = "Mode is required")
    private String mode;

    @JsonProperty("filename")
    @NotBlank(message = "Filename is required")
    private String filename;

    @JsonProperty("filesize")
    @Positive(message = "File size must be positive")
    private Long filesize;

    @JsonProperty("mediatype")
    @NotBlank(message = "Media type is required")
    private String mediatype;

    @JsonProperty("width")
    @Positive(message = "Width must be positive")
    private Integer width;

    @JsonProperty("height")
    @Positive(message = "Height must be positive")
    private Integer height;

    @JsonProperty("metahash")
    @NotBlank(message = "Meta hash is required")
    @Size(min = 64, max = 128, message = "Meta hash must be between 64 and 128 characters")
    private String metahash;

    @JsonProperty("datahash")
    @NotBlank(message = "Data hash is required")
    @Size(min = 64, max = 128, message = "Data hash must be between 64 and 128 characters")
    private String datahash;

    @JsonProperty("thumbnail")
    private String thumbnail;

    // Default constructor
    public ISCC() {
    }

    // Constructor with required fields
    public ISCC(
            String context,
            String type,
            String schema,
            String iscc,
            String content,
            String mediaId,
            String name,
            String mode,
            String filename,
            Long filesize,
            String mediatype,
            Integer width,
            Integer height,
            String metahash,
            String datahash,
            String thumbnail
    ) {
        this.context = context;
        this.type = type;
        this.schema = schema;
        this.iscc = iscc;
        this.content = content;
        this.mediaId = mediaId;
        this.name = name;
        this.mode = mode;
        this.filename = filename;
        this.filesize = filesize;
        this.mediatype = mediatype;
        this.width = width;
        this.height = height;
        this.metahash = metahash;
        this.datahash = datahash;
        this.thumbnail = thumbnail;
    }

    // Getters
    public String getContext() {
        return context;
    }

    public String getType() {
        return type;
    }

    public String getSchema() {
        return schema;
    }

    public String getIscc() {
        return iscc;
    }

    public String getContent() {
        return content;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public String getFilename() {
        return filename;
    }

    public Long getFilesize() {
        return filesize;
    }

    public String getMediatype() {
        return mediatype;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getMetahash() {
        return metahash;
    }

    public String getDatahash() {
        return datahash;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    // Setters
    public void setContext(String context) {
        this.context = context;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setIscc(String iscc) {
        this.iscc = iscc;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setMetahash(String metahash) {
        this.metahash = metahash;
    }

    public void setDatahash(String datahash) {
        this.datahash = datahash;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public static ISCC parse(JSONObject json) {
        return new ISCC(
                json.getString("@context"),
                json.getString("@type"),
                json.getString("$schema"),
                json.getString("iscc"),
                json.getString("content"),
                json.getString("media_id"),
                json.getString("name"),
                json.getString("mode"),
                json.getString("filename"),
                json.getLong("filesize"),
                json.getString("mediatype"),
                json.getInt("width"),
                json.getInt("height"),
                json.getString("metahash"),
                json.getString("datahash"),
                json.getString("thumbnail")
        );
    }

    @Override
    public String toString() {
        return "ISCC{" + "context=" + context 
                + ", type=" + type 
                + ", schema=" + schema 
                + ", iscc=" + iscc 
                + ", content=" + content 
                + ", mediaId=" + mediaId 
                + ", name=" + name 
                + ", mode=" + mode 
                + ", filename=" + filename 
                + ", filesize=" + filesize 
                + ", mediatype=" + mediatype 
                + ", width=" + width
                + ", height=" + height 
                + ", metahash=" + metahash 
                + ", datahash=" + datahash 
                + ", thumbnail=" + thumbnail + '}';
    }
}
