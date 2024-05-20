/*
 * This file is generated by jOOQ.
 */
package net.joostvdg.tektonvisualizer.model.tables.records;


import net.joostvdg.tektonvisualizer.model.tables.CodeSource;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CodeSourceRecord extends UpdatableRecordImpl<CodeSourceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.code_source.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.code_source.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.code_source.source_type</code>.
     */
    public void setSourceType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.code_source.source_type</code>.
     */
    public String getSourceType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.code_source.source_url</code>.
     */
    public void setSourceUrl(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.code_source.source_url</code>.
     */
    public String getSourceUrl() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.code_source.sub_path</code>.
     */
    public void setSubPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.code_source.sub_path</code>.
     */
    public String getSubPath() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeSourceRecord
     */
    public CodeSourceRecord() {
        super(CodeSource.CODE_SOURCE);
    }

    /**
     * Create a detached, initialised CodeSourceRecord
     */
    public CodeSourceRecord(Integer id, String sourceType, String sourceUrl, String subPath) {
        super(CodeSource.CODE_SOURCE);

        setId(id);
        setSourceType(sourceType);
        setSourceUrl(sourceUrl);
        setSubPath(subPath);
        resetChangedOnNotNull();
    }
}