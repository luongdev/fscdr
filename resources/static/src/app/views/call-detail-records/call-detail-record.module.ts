import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {CallDetailRecordRoutingModule} from './call-detail-record-routing.module';
import {CallDetailRecordTableModule} from "./call-detail-record-table/call-detail-record-table.module";

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        CallDetailRecordTableModule,
        CallDetailRecordRoutingModule
    ]
})
export class CallDetailRecordModule {
}
