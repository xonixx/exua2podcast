<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xspf="http://xspf.org/ns/0/">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="xspf:playlist">
        <rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" version="2.0">
            <channel>
                <title><xsl:value-of select="xspf:title"/></title>
                <link><xsl:value-of select="xspf:location"/></link>
                <xsl:if test="xspf:img">
                    <itunes:image href="{xspf:img}"/>
                </xsl:if>
            </channel>

            <xsl:apply-templates select="xspf:trackList/xspf:track"/>
        </rss>
    </xsl:template>
    <xsl:template match="xspf:track">
        <item>
            <title><xsl:value-of select="xspf:title"/></title>
            <enclosure url="{xspf:location}" type="audio/mpeg" />
        </item>
    </xsl:template>
</xsl:stylesheet>