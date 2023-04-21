export class ColumnTableModel {
  constructor(
    public id: number,
    public field: string,
    public header: string,
    public center: boolean,
    public style: {width: string},
    public isFrozen: boolean
  ) {}
}
