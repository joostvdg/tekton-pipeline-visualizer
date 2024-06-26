/*
 * This file is generated by jOOQ.
 */
package net.joostvdg.tektonvisualizer.model.tables;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.joostvdg.tektonvisualizer.model.Indexes;
import net.joostvdg.tektonvisualizer.model.Keys;
import net.joostvdg.tektonvisualizer.model.Public;
import net.joostvdg.tektonvisualizer.model.tables.CodeSource.CodeSourcePath;
import net.joostvdg.tektonvisualizer.model.tables.PipelineStatus.PipelineStatusPath;
import net.joostvdg.tektonvisualizer.model.tables.PipelineStatusSupplyChain.PipelineStatusSupplyChainPath;
import net.joostvdg.tektonvisualizer.model.tables.SupplyChainCodeSource.SupplyChainCodeSourcePath;
import net.joostvdg.tektonvisualizer.model.tables.records.SupplyChainRecord;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class SupplyChain extends TableImpl<SupplyChainRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.supply_chain</code>
     */
    public static final SupplyChain SUPPLY_CHAIN = new SupplyChain();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SupplyChainRecord> getRecordType() {
        return SupplyChainRecord.class;
    }

    /**
     * The column <code>public.supply_chain.id</code>.
     */
    public final TableField<SupplyChainRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.supply_chain.name</code>.
     */
    public final TableField<SupplyChainRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private SupplyChain(Name alias, Table<SupplyChainRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private SupplyChain(Name alias, Table<SupplyChainRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.supply_chain</code> table reference
     */
    public SupplyChain(String alias) {
        this(DSL.name(alias), SUPPLY_CHAIN);
    }

    /**
     * Create an aliased <code>public.supply_chain</code> table reference
     */
    public SupplyChain(Name alias) {
        this(alias, SUPPLY_CHAIN);
    }

    /**
     * Create a <code>public.supply_chain</code> table reference
     */
    public SupplyChain() {
        this(DSL.name("supply_chain"), null);
    }

    public <O extends Record> SupplyChain(Table<O> path, ForeignKey<O, SupplyChainRecord> childPath, InverseForeignKey<O, SupplyChainRecord> parentPath) {
        super(path, childPath, parentPath, SUPPLY_CHAIN);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class SupplyChainPath extends SupplyChain implements Path<SupplyChainRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> SupplyChainPath(Table<O> path, ForeignKey<O, SupplyChainRecord> childPath, InverseForeignKey<O, SupplyChainRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private SupplyChainPath(Name alias, Table<SupplyChainRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public SupplyChainPath as(String alias) {
            return new SupplyChainPath(DSL.name(alias), this);
        }

        @Override
        public SupplyChainPath as(Name alias) {
            return new SupplyChainPath(alias, this);
        }

        @Override
        public SupplyChainPath as(Table<?> alias) {
            return new SupplyChainPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.SUPPLY_CHAIN_NAME);
    }

    @Override
    public Identity<SupplyChainRecord, Integer> getIdentity() {
        return (Identity<SupplyChainRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<SupplyChainRecord> getPrimaryKey() {
        return Keys.SUPPLY_CHAIN_PKEY;
    }

    private transient PipelineStatusSupplyChainPath _pipelineStatusSupplyChain;

    /**
     * Get the implicit to-many join path to the
     * <code>public.pipeline_status_supply_chain</code> table
     */
    public PipelineStatusSupplyChainPath pipelineStatusSupplyChain() {
        if (_pipelineStatusSupplyChain == null)
            _pipelineStatusSupplyChain = new PipelineStatusSupplyChainPath(this, null, Keys.PIPELINE_STATUS_SUPPLY_CHAIN__PIPELINE_STATUS_SUPPLY_CHAIN_SUPPLY_CHAIN_ID_FKEY.getInverseKey());

        return _pipelineStatusSupplyChain;
    }

    private transient SupplyChainCodeSourcePath _supplyChainCodeSource;

    /**
     * Get the implicit to-many join path to the
     * <code>public.supply_chain_code_source</code> table
     */
    public SupplyChainCodeSourcePath supplyChainCodeSource() {
        if (_supplyChainCodeSource == null)
            _supplyChainCodeSource = new SupplyChainCodeSourcePath(this, null, Keys.SUPPLY_CHAIN_CODE_SOURCE__SUPPLY_CHAIN_CODE_SOURCE_SUPPLY_CHAIN_ID_FKEY.getInverseKey());

        return _supplyChainCodeSource;
    }

    /**
     * Get the implicit many-to-many join path to the
     * <code>public.pipeline_status</code> table
     */
    public PipelineStatusPath pipelineStatus() {
        return pipelineStatusSupplyChain().pipelineStatus();
    }

    /**
     * Get the implicit many-to-many join path to the
     * <code>public.code_source</code> table
     */
    public CodeSourcePath codeSource() {
        return supplyChainCodeSource().codeSource();
    }

    @Override
    public SupplyChain as(String alias) {
        return new SupplyChain(DSL.name(alias), this);
    }

    @Override
    public SupplyChain as(Name alias) {
        return new SupplyChain(alias, this);
    }

    @Override
    public SupplyChain as(Table<?> alias) {
        return new SupplyChain(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public SupplyChain rename(String name) {
        return new SupplyChain(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SupplyChain rename(Name name) {
        return new SupplyChain(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public SupplyChain rename(Table<?> name) {
        return new SupplyChain(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain where(Condition condition) {
        return new SupplyChain(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SupplyChain where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SupplyChain where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SupplyChain where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SupplyChain where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SupplyChain whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
