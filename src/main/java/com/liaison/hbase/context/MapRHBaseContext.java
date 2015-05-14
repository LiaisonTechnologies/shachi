package com.liaison.hbase.context;


public class MapRHBaseContext extends CommonHBaseContext {

    public static final class Builder extends AbstractHBaseContextBuilder<MapRHBaseContext, Builder> {
        @Override
        public Builder self() {
            return this;
        }
        @Override
        public MapRHBaseContext build() {
            return new MapRHBaseContext(this);
        }
        private Builder() { }
    }
    
    private static final String DEFAULT_MAPRTABLES_PREFIX = "/user/mapr/tables/";
    private static final TableNamingStrategy
        DEFAULT_TABLENAMINGSTRATEGY =
            new DirectoryPrefixedTableNamingStrategy(DEFAULT_MAPRTABLES_PREFIX);

    public static final Builder getBuilder() {
        return new Builder();
    }
    
    @Override
    protected final TableNamingStrategy getDefaultTableNamingStrategy() {
        return DEFAULT_TABLENAMINGSTRATEGY;
    }
    
    public MapRHBaseContext(final Builder build) {
        super(build);
    }
}
