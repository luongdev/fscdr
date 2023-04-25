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
import {NzTableModule} from "ng-zorro-antd/table";
import {NzInputModule} from "ng-zorro-antd/input";
import {NzButtonModule} from "ng-zorro-antd/button";
import {NzIconModule} from "ng-zorro-antd/icon";
import {NzBadgeModule} from "ng-zorro-antd/badge";

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

const ANTD = [
    NzTableModule,
    NzInputModule,
    NzButtonModule,
    NzIconModule,
    NzBadgeModule
];

@NgModule({
    declarations: [
        CallDetailRecordPreviewComponent
    ],
    imports: [
        CommonModule,
        ...PRIMENG,
        ...ANTD
    ],
    exports: [CallDetailRecordPreviewComponent]
})
export class CallDetailRecordPreviewModule {
}
