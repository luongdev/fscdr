import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {TableModule} from "primeng/table";
import {PaginatorModule} from "primeng/paginator";
import {ToastModule} from "primeng/toast";
import {CallDetailRecordTableComponent} from "./call-detail-record-table.component";
import {CalendarModule} from "primeng/calendar";
import {BadgeModule} from "primeng/badge";
import {DialogModule} from "primeng/dialog";
import {CallDetailRecordPreviewModule} from "../call-detail-record-preview/call-detail-record-preview.module";

const PRIMENG = [
    ButtonModule,
    TableModule,
    ToastModule,
    PaginatorModule,
    InputTextModule,
    CalendarModule,
    BadgeModule,
    DialogModule
];

@NgModule({
    declarations: [CallDetailRecordTableComponent],
    imports: [CommonModule, FormsModule, ...PRIMENG, CallDetailRecordPreviewModule],
    exports: [CallDetailRecordTableComponent]
})
export class CallDetailRecordTableModule {
}
