<!--
  Overrides the default 2xhtml in mm:xslt/2xhtml.xslt

  (Show the date if there is one in your news type) and makes the
  title red)

  @author Michiel Meeuwissen   
  @version $Id: 2xhtml.xslt,v 1.9 2004-02-11 20:02:04 michiel Exp $
  @since  MMBase-1.6
  
-->
<xsl:stylesheet 
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" 
  version = "1.0" 
  xmlns:mmxf="http://www.mmbase.org/mmxf"
  exclude-result-prefixes="mmxf" 
>
  <xsl:import href="mm:xslt/2xhtml.xslt" />  
  <xsl:output method="xml" omit-xml-declaration="yes"  /><!-- xhtml is a form of xml -->
    
  <xsl:param name="subtitle_color">green</xsl:param>
  <xsl:param name="formatter_counter"></xsl:param>

  <xsl:template name="formatteddate">
	<xsl:param name="year"     />
	<xsl:param name="monthname" />
	<xsl:param name="day"    />
	-<xsl:value-of select="concat($day,' ',$monthname,' ',$year)" />-
  </xsl:template>

  <!-- how to present a news node -->
  <xsl:template match="object[@type=$newstype and not(unfilledField)]">
    <xsl:apply-templates select="field[@name='title']"  />
      <xsl:if test="not(field[@name='subtitle'] = '')">
        <font color="{$subtitle_color}"><xsl:apply-templates select="field[@name='subtitle']" /></font>
      </xsl:if>
      <div class="toc">
        table of contents:<br />
        <xsl:for-each select="field[@name='body']/mmxf/section">
          <a>
            <xsl:attribute name="href">#<xsl:value-of select="$formatter_counter" /><xsl:value-of select="generate-id(.)" /></xsl:attribute>
            <xsl:value-of select="@title" />
            </a><br />
        </xsl:for-each>
      </div>
	<xsl:apply-templates select="field[@name='body']" />
	<p>
      <xsl:call-template name="date">     
	    <xsl:with-param name="datetime" select="field[@name='date']" />
      </xsl:call-template>
      </p>
  </xsl:template>

   <xsl:template match = "h" >
     <xsl:if test="count(ancestor::section)=1">
       <h3 style="color:red;">
         <a>
           <xsl:attribute name="id">
             <xsl:value-of select="$formatter_counter" /><xsl:value-of select="generate-id(.)" />
           </xsl:attribute>
           <xsl:value-of select="." />
         </a>
       </h3>
     </xsl:if>
     <xsl:if test="count(ancestor::section)=2"><p><strong><xsl:value-of select="." /></strong></p></xsl:if>
     <xsl:if test="count(ancestor::section)=3"><p><xsl:value-of select="." /></p></xsl:if>
     <xsl:if test="count(ancestor::section)>3"><xsl:value-of select="." /><br /></xsl:if>
   </xsl:template>
     
</xsl:stylesheet>
