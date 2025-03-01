/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;
import org.cmdbuild.lookup.IconType;
import org.cmdbuild.lookup.LookupConfig;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * See {@link LookupValueWsCommons#toResponse(LookupValue)}
 * 
 * @author afelice
 */
public class LookupValueConfiguration {
        
    @JsonProperty("_id")
    private final Long id; 
        
    @JsonProperty("_type")    
    private final String lookupName;    
    private final String code;
    private final String description;
    
    @JsonProperty("_description_translation")
    private String descriptionTranslation;

    @JsonProperty("note") // serialization is in field named "note", see LookupValueWsCommons.toResponse(LookupValue)
    public String notes;
    public Integer index;
    public boolean active;
    
//    @JsonProperty("parent_id")
//    public Long parentId;
    
    @JsonProperty("parent_type")
    public String lookupParentName;    
    
    @JsonProperty("default")
    public boolean isDefault;
    
    @JsonIgnore
    protected IconType iconType;
    
    protected String iconImage;
    protected String iconFont;
    protected String iconColor;
    protected String textColor;    
    
    @JsonCreator
    public LookupValueConfiguration(Long id, String lookupName, String code, String description) {
        this.id = id;
        this.lookupName = checkNotBlank(lookupName);
        this.code = checkNotBlank(code);
        this.description = checkNotBlank(description);
    }

    public Long getId() {
        return id;
    }
    
    public String getLookupName() {
        return lookupName;
    }    
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }    
    
    public void setDescriptionTranslation(String descriptionTranslation) {
        this.descriptionTranslation = checkNotBlank(descriptionTranslation);
    }
    
    public String getDescriptionTranslation() {
        return descriptionTranslation;
    }      
    
    @JsonIgnore
    public IconType getIconType() {
        return iconType;
    }

    @JsonProperty("iconType")
    public String getIconTypeStr() {
        return serializeEnum(iconType);
    }

    public void setIconType(IconType iconType) {
        this.iconType = iconType;
    }
   
    // Wan't be serialized
//    @JsonProperty("iconType")
//    public void setIconTypeStr(String typeStr) {
//        this.iconType = parseEnumOrDefault(typeStr, IconType.NONE);
//    }    
   
    @Nullable
    public String getTextColor() {
        return textColor;
    }
    
    @Nullable
    public String getIconImage() {
        return iconImage;
    }

    @Nullable
    public String getIconFont() {
        return iconFont;
    }

    @Nullable
    public String getIconColor() {
        return iconColor;
    }     

    public void applyConfig(LookupConfig config) {
        iconColor = config.getIconColor();
        iconFont = config.getIconFont();
        iconImage = config.getIconImage();
        iconType = config.getIconType();
        textColor = config.getTextColor();
    }
    
}
