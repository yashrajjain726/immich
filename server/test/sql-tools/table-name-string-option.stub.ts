import { DatabaseSchema, Table } from 'src/sql-tools';

@Table('table-1')
export class Table1 {}

export const description = 'should register a table with a specific name';
export const schema: DatabaseSchema = {
  databaseName: 'postgres',
  schemaName: 'public',
  functions: [],
  enums: [],
  extensions: [],
  parameters: [],
  overrides: [],
  tables: [
    {
      name: 'table-1',
      columns: [],
      indexes: [],
      triggers: [],
      constraints: [],
      synchronize: true,
    },
  ],
  warnings: [],
};
