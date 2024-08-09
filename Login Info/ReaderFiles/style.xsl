<?xml version="1.0"?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" > 
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/> 
    <xsl:template match="/"> 
        <xsl:text>name,logAmount,type,dateTime,permission,timeOnline</xsl:text>
        <xsl:text>&#xA;</xsl:text>
        <xsl:for-each select="//user"> 
            <xsl:variable name="name" select="name"/>
            <xsl:variable name="log_amount" select="logAmount"/>
            <xsl:for-each select="./info"> 
                <xsl:value-of select="concat($name,',', $log_amount,',', type,',', dateTime,',', permission,',', timeOnline)"/>
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each> 
        </xsl:for-each> 
    </xsl:template> 
</xsl:stylesheet> 