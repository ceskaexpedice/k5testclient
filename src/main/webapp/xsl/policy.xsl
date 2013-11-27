<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xalan/java"
                version="1.0">
    <xsl:output method="html"/>
    
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="count(/response/lst[@name='facet_counts']/lst[@name='facet_fields']/lst/int)=2" >0</xsl:when>
            <xsl:when test="/response/lst[@name='facet_counts']/lst[@name='facet_fields']/lst/int/@name='public'" >1</xsl:when>
            <xsl:when test="/response/lst[@name='facet_counts']/lst[@name='facet_fields']/lst/int/@name='private'" >2</xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
