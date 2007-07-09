package liquibase.migrator.change;

import liquibase.database.*;
import liquibase.migrator.exception.UnsupportedChangeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sets a new default value to an existing column.
 */
public class AddDefaultValueChange extends AbstractChange {
    private String tableName;
    private String columnName;
    private String defaultValue;

    public AddDefaultValueChange() {
        super("addDefaultValue", "Add Default Value");
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] generateStatements(Database database) throws UnsupportedChangeException {

        String maybeQuotedValue;
        if ("NULL".equalsIgnoreCase(getDefaultValue())) {
            maybeQuotedValue = getDefaultValue();
        } else {
            maybeQuotedValue = "'" + getDefaultValue() + "'";
        }


        if (database instanceof MSSQLDatabase) {
            return new String[]{ "ALTER TABLE " + getTableName() + " WITH NOCHECK ADD CONSTRAINT " + getColumnName() + "DefaultValue DEFAULT "+maybeQuotedValue+" FOR " + getColumnName(), };
        } else if (database instanceof MySQLDatabase) {
            return new String[]{ "ALTER TABLE " + getTableName() + " ALTER " + getColumnName() + " SET DEFAULT "+maybeQuotedValue, };
        } else if (database instanceof OracleDatabase) {
            return new String[]{ "ALTER TABLE " + getTableName() + " MODIFY " + getColumnName() + " DEFAULT "+maybeQuotedValue, };
        } else if (database instanceof DerbyDatabase) {
            return new String[]{ "ALTER TABLE " + getTableName() + " ALTER COLUMN  " + getColumnName() + " WITH DEFAULT '" + getDefaultValue() + "'", };
        }

        return new String[]{
                "ALTER TABLE " + getTableName() + " ALTER COLUMN  " + getColumnName() + " SET DEFAULT "+maybeQuotedValue,
        };
    }

    protected Change[] createInverses() {
        DropDefaultValueChange inverse = new DropDefaultValueChange();
        inverse.setTableName(getTableName());
        inverse.setColumnName(getColumnName());

        return new Change[]{
                inverse
        };
    }

    public String getConfirmationMessage() {
        return "Default Value Added";
    }

    public Element createNode(Document currentChangeLogFileDOM) {
        Element node = currentChangeLogFileDOM.createElement(getTagName());
        node.setAttribute("tableName", getTableName());
        node.setAttribute("columnName", getColumnName());
        node.setAttribute("defaultValue", getDefaultValue());

        return node;
    }
}
