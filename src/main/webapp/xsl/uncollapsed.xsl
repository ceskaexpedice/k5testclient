<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    <xsl:output method="html" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />
    <xsl:param name="bundle_url" select="bundle_url" />
    <xsl:param name="bundle" select="document($bundle_url)/bundle" />
    <xsl:param name="root_pid" select="root_pid"/>
    <xsl:param name="pid" select="pid"/>
    <xsl:param name="q" select="q"/>
    <xsl:template match="/">
        <xsl:if test="//doc" >
            <div><xsl:attribute name="id">uncoll_<xsl:value-of select="$root_pid"/></xsl:attribute>
                <xsl:for-each select="//doc" >
                    <xsl:call-template name="rels">
                        <xsl:with-param name="fmodel"><xsl:value-of select="./str[@name='fedora.model']" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </div>
            <xsl:call-template name="pagination">
                <xsl:with-param name="rows"><xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='rows']" /></xsl:with-param>
                <xsl:with-param name="start"><xsl:value-of select="/response/result/@start" /></xsl:with-param>
                <xsl:with-param name="numDocs"><xsl:value-of select="/response/result/@numFound" /></xsl:with-param>
            </xsl:call-template>
         </xsl:if>
    </xsl:template>

    <xsl:template name="rels">
        <xsl:param name="fmodel" />
            <xsl:variable name="solruuid"><xsl:value-of select="./str[@name='PID']"/></xsl:variable>
            <div>
                <xsl:attribute name="class">r<xsl:value-of select="position() mod 2"/></xsl:attribute>
                <a><xsl:attribute name="href">doc.vm?pid=<xsl:value-of select="./str[@name='PID']"/>&amp;q=<xsl:value-of select="$q"/>
                </xsl:attribute>
                <xsl:call-template name="details">
                    <xsl:with-param name="fmodel"><xsl:value-of select="$fmodel" /></xsl:with-param>
                </xsl:call-template>
                </a>
                <!--
                <xsl:if test="./str[@name='datum_str']/text()">
                    <div><xsl:value-of select="./str[@name='datum_str']" /></div>
                </xsl:if>
                -->
            <div class="teaser">
                <xsl:for-each select="../../lst[@name='highlighting']/lst">
                    <xsl:if test="@name = $solruuid">
                        <xsl:for-each select="./arr[@name='text_ocr']/str">
                        (... <xsl:value-of select="." disable-output-escaping="yes" /> ...)<br/>
                        </xsl:for-each>
                    </xsl:if>
                </xsl:for-each>
            </div>
            </div>

    </xsl:template>


    <xsl:template name="details">
        <xsl:param name="fmodel" />
            <xsl:choose>
                <xsl:when test="$fmodel='monograph'">
                    <xsl:value-of select="./str[@name='dc.title']" />&#160;
                </xsl:when>
                <xsl:when test="$fmodel='monographunit'">
                    <xsl:value-of select="$bundle/value[@key='fedora.model.monographunit']"/>:&#160;
                    <xsl:value-of select="./str[@name='dc.title']" />&#160;
                    <xsl:call-template name="monographunit">
                        <xsl:with-param name="detail"><xsl:value-of select="./arr[@name='details']/str" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='periodical'">
                    <xsl:value-of select="./str[@name='dc.title']" />
                </xsl:when>
                <xsl:when test="$fmodel='periodicalvolume'">
                    <xsl:call-template name="periodicalvolume">
                        <xsl:with-param name="detail"><xsl:value-of select="./arr[@name='details']/str" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='periodicalitem'">
                    <xsl:call-template name="periodicalitem">
                        <xsl:with-param name="detail"><xsl:value-of select="./arr[@name='details']/str" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='internalpart'">
                    <xsl:value-of select="$bundle/value[@key='fedora.model.internalpart']"/>:&#160;
                    <xsl:value-of select="dc.title" />&#160;
                    <xsl:call-template name="internalpart">
                        <xsl:with-param name="detail"><xsl:value-of select="./arr[@name='details']/str" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='page'">
                    <xsl:call-template name="page">
                        <xsl:with-param name="detail"><xsl:value-of select="./arr[@name='details']/str" /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$bundle/value[@key=./arr[@name='details']/str]"/>&#160;
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>

    <xsl:template name="periodicalvolume">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key='Datum vydání']"/>:
        <xsl:value-of select="substring-before($detail, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key='Číslo']"/>
        <xsl:value-of select="substring-after($detail, '##')" />
    </xsl:template>

    <xsl:template name="periodicalitem">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key='fedora.model.periodicalitem']"/>&#160;
        <xsl:if test="substring-before($detail, '##')"><xsl:value-of select="substring-before($detail, '##')" /></xsl:if>
        <xsl:variable name="remaining" select="substring-after($detail, '##')" />
        <xsl:value-of select="substring-before($remaining, '##')" /><br/>
        <xsl:variable name="remaining2" select="substring-after($remaining, '##')" />
        <xsl:value-of select="$bundle/value[@key='Datum vydání']"/>:&#160;
        <xsl:value-of select="substring-before($remaining2, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key='Číslo']"/>&#160;
        <xsl:value-of select="substring-after($remaining2, '##')" />
    </xsl:template>

    <xsl:template name="monographunit">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key=substring-before($detail, '##')]"/>&#160;
        <xsl:value-of select="$bundle/value[@key=substring-after($detail, '##')]"/>
    </xsl:template>

    <xsl:template name="internalpart">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key=substring-before($detail, '##')]"/>:&#160;
        <xsl:variable name="remaining" select="substring-after($detail, '##')" />
        <xsl:value-of select="substring-before($remaining, '##')" />&#160;
        <xsl:variable name="remaining2" select="substring-after($remaining, '##')" />
        <xsl:value-of select="substring-before($remaining2, '##')" />&#160;
        <xsl:value-of select="substring-after($remaining2, '##')" />
    </xsl:template>

    <xsl:template name="page">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key='fedora.model.page']"/>&#160;
        <xsl:value-of select="substring-before($detail, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key=substring-after($detail, '##')]"/>
    </xsl:template>

<!-- pagination -->
<xsl:template name="pagination">
        <xsl:param name="rows"/>
        <xsl:param name="start"/>
        <xsl:param name="numDocs"/>
        <xsl:if test="$numDocs &gt; 0">
            <xsl:variable name="pageStart"><xsl:choose>
                <xsl:when test="$start - $rows*3 &lt; 1">1</xsl:when>
                <xsl:otherwise><xsl:value-of select="$start - $rows*3" /></xsl:otherwise>
            </xsl:choose></xsl:variable>
            <div style="float:right;" class="pagination">
                <xsl:if test="$start &gt; $rows">
                    <a class="previous">
                        <xsl:attribute name="href">javascript:uncollapse('<xsl:value-of select="$root_pid" />', 
                    '<xsl:value-of select="$pid" />', 
                    <xsl:value-of select="$start - $rows - 1" />)</xsl:attribute> &lt;&lt;
                    </a>
                </xsl:if>
                <xsl:call-template name="pag_page">
                    <xsl:with-param name="pageNum"><xsl:value-of select="1" /></xsl:with-param>
                    <xsl:with-param name="rows"><xsl:value-of select="$rows" /></xsl:with-param>
                    <xsl:with-param name="start"><xsl:value-of select="$pageStart" /></xsl:with-param>
                    <xsl:with-param name="numDocs"><xsl:value-of select="$numDocs" /></xsl:with-param>
                    <xsl:with-param name="firsthit"><xsl:value-of select="$start" /></xsl:with-param>
                </xsl:call-template>
                <xsl:if test="$numDocs &gt; $rows + $start">
                    <a class="next"><xsl:attribute name="href">javascript:uncollapse('<xsl:value-of select="$root_pid" />', 
                    '<xsl:value-of select="$pid" />', 
                    <xsl:value-of select="$rows + $start - 1" />)</xsl:attribute> &gt;&gt;
                    </a>
                </xsl:if>
            </div>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="pag_page">
        <xsl:param name="pageNum"/>
        <xsl:param name="rows"/>
        <xsl:param name="start"/>
        <xsl:param name="numDocs"/>
        <xsl:param name="firsthit"/>
        &#160;<a>
            <xsl:attribute name="href">javascript:uncollapse('<xsl:value-of select="$root_pid" />', '<xsl:value-of select="$pid" />', <xsl:value-of select="$start - 1" />);</xsl:attribute>
            <xsl:if test="$start = $firsthit">
                <xsl:attribute name="class">sel</xsl:attribute>
            </xsl:if><span><xsl:value-of select="$start" />-<xsl:choose>
            <xsl:when test="$numDocs &gt; $rows + $start"><xsl:value-of select="$rows + $start - 1" /></xsl:when>
            <xsl:otherwise><xsl:value-of select="$numDocs" /></xsl:otherwise></xsl:choose></span></a>&#160;|
        <xsl:if test="($pageNum &lt; 7) and ($numDocs &gt; $start + $rows)">
            <xsl:call-template name="pag_page">
                <xsl:with-param name="pageNum"><xsl:value-of select="$pageNum + 1" /></xsl:with-param>
                <xsl:with-param name="rows"><xsl:value-of select="$rows" /></xsl:with-param>
                <xsl:with-param name="start"><xsl:value-of select="$start + $rows" /></xsl:with-param>
                <xsl:with-param name="numDocs"><xsl:value-of select="$numDocs" /></xsl:with-param>
                    <xsl:with-param name="firsthit"><xsl:value-of select="$firsthit" /></xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
