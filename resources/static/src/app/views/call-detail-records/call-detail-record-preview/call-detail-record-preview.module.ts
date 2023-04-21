import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {CallDetailRecordPreviewComponent} from './call-detail-record-preview.component';

import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {PaginatorModule} from "primeng/paginator";
import {ToastModule} from "primeng/toast";
import {CalendarModule} from "primeng/calendar";
import {BadgeModule} from "primeng/badge";
import {DialogModule} from "primeng/dialog";

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
    declarations: [
        CallDetailRecordPreviewComponent
    ],
    imports: [
        CommonModule,
        ...PRIMENG
    ],
    exports: [CallDetailRecordPreviewComponent]
})
export class CallDetailRecordPreviewModule {
}
