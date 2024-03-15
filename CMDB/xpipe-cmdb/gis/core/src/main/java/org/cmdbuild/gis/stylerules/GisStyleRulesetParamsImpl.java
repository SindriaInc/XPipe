/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;

public class GisStyleRulesetParamsImpl implements GisStyleRulesetParams {

    private final GisStyleRulesetAnalysisType analysisType;
    private final Integer segments;
    private final String classAttribute;

    private GisStyleRulesetParamsImpl(GisStyleRulesetParamsImplBuilder builder) {
        this.analysisType = builder.analysisType;
        this.segments = builder.segments;
        this.classAttribute = builder.classAttribute;
    }

    @JsonCreator
    public GisStyleRulesetParamsImpl(@Nullable @JsonProperty("analysisType") String analysisType, @Nullable @JsonProperty("classAttribute") String classAttribute, @Nullable @JsonProperty("segments") Integer segments) {
        this.analysisType = parseEnumOrNull(analysisType, GisStyleRulesetAnalysisType.class);
        this.segments = toIntegerOrNull(segments);
        this.classAttribute = classAttribute;
    }

    @Nullable
    @Override
    public GisStyleRulesetAnalysisType getAnalysisType() {
        return analysisType;
    }

    @Nullable
    @Override
    public Integer getSegments() {
        return segments;
    }

    @Nullable
    @Override
    public String getClassAttribute() {
        return classAttribute;
    }

    public static GisStyleRulesetParamsImplBuilder builder() {
        return new GisStyleRulesetParamsImplBuilder();
    }

    public static GisStyleRulesetParamsImplBuilder copyOf(GisStyleRulesetParams source) {
        return new GisStyleRulesetParamsImplBuilder()
                .withAnalysisType(source.getAnalysisType())
                .withSegments(source.getSegments())
                .withClassAttribute(source.getClassAttribute());
    }

    public static class GisStyleRulesetParamsImplBuilder implements Builder<GisStyleRulesetParamsImpl, GisStyleRulesetParamsImplBuilder> {

        private GisStyleRulesetAnalysisType analysisType;
        private Integer segments;
        private String classAttribute;

        public GisStyleRulesetParamsImplBuilder withAnalysisType(GisStyleRulesetAnalysisType analysisType) {
            this.analysisType = analysisType;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withSegments(Integer segments) {
            this.segments = segments;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withClassAttribute(String classAttribute) {
            this.classAttribute = classAttribute;
            return this;
        }

        @Override
        public GisStyleRulesetParamsImpl build() {
            return new GisStyleRulesetParamsImpl(this);
        }

    }
}
