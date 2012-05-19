package com.github.goldin.plugins.jenkins.markup

import static com.github.goldin.plugins.common.GMojoUtils.*
import groovy.xml.MarkupBuilder
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Abstract class for all {@link groovy.xml.MarkupBuilder} based markup builders.
 */
abstract class Markup
{
    /**
     * XML markup indentation.
     */
    public static final String INDENT = ' ' * 4

    /**
     * Quotation HTML entity.
     */
    public static final String QUOT = '&quot;'


    private final Writer writer
    final MarkupBuilder  builder

    @Ensures({ this.writer && this.builder })
    Markup()
    {
        this.writer          = new StringWriter ( 4 * 1024 )
        this.builder         = new MarkupBuilder( new IndentPrinter( writer, INDENT ))
        builder.doubleQuotes = true
    }


    @Requires({ builder })
    @Ensures({ this.builder })
    Markup( MarkupBuilder builder )
    {
        this.writer  = null
        this.builder = builder
    }


    /**
     * Builds a markup using {@link #builder}.
     */
    abstract void buildMarkup()


    /**
     * Helper method, a {@link groovy.xml.MarkupBuilderHelper#yieldUnescaped} wrapper - adds a value specified
     * to the {@link #builder}, unescaped, if it evaluates to Groovy {@code true}.
     *
     * @param value value to add to the {@link #builder}, unescaped.
     */
    @Requires({ value })
    final void add ( String value ) { if ( value ) { builder.mkp.yieldUnescaped( value ) }}


    /**
     * Adds tag and a value to the {@link #builder} if value evaluates to Groovy {@code true}.
     * @param tagName name of the tag to add
     * @param value   tag's value to add
     */
    @Requires({ tagName })
    final void add ( String tagName, Object value ) { if ( value ) { builder."$tagName"( value ) }}


    /**
     * Helper methods
     */
    String code   ( String   expression )           { tag( 'code',   QUOT + expression + QUOT )}
    String strong ( String   expression )           { tag( 'strong', expression )}
    String tag    ( String   tagName, String value ){ "<$tagName>$value</$tagName>" }


    /**
     * Retrieves a markup generated with the builder.
     * @return markup generated with the builder
     */
    @Requires({ builder })
    @Ensures({ result })
    final String getMarkup()
    {
        assert this.writer, "This instance was created using another MarkupBuilder, there's no access to the writer"

        buildMarkup()
        verify().notNullOrEmpty( this.writer.toString())
    }
}