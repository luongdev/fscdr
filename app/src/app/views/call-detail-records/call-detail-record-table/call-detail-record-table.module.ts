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
import {NzButtonModule} from 'ng-zorro-antd/button';
import {NzBadgeModule} from 'ng-zorro-antd/badge';
import {NzDatePickerModule} from "ng-zorro-antd/date-picker";
import {NzIconModule} from "ng-zorro-antd/icon";
import {NzInputModule} from "ng-zorro-antd/input";
import {NzSpaceModule} from "ng-zorro-antd/space";
import {NzSelectModule} from "ng-zorro-antd/select";


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
    imports: [
        CommonModule,
        FormsModule,
        ...PRIMENG,
        NzButtonModule,
        CallDetailRecordPreviewModule,
        NzDatePickerModule,
        NzBadgeModule,
        NzIconModule,
        NzInputModule,
        NzSpaceModule,
        NzSelectModule,
    ],
    exports: [CallDetailRecordTableComponent]
})
export class CallDetailRecordTableModule {
}
