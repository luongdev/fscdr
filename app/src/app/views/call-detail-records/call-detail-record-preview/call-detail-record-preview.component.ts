import {Component, Injector, OnInit} from '@angular/core';
import {ComponentBase} from "@shared/ultils/component-base.component";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";
import {DynamicDialogConfig} from "primeng/dynamicdialog";

@Component({
    selector: 'app-call-detail-record-preview',
    templateUrl: './call-detail-record-preview.component.html',
})
export class CallDetailRecordPreviewComponent extends ComponentBase<CallDetailRecordList> implements OnInit {

    constructor(injector: Injector, dialogConfig: DynamicDialogConfig) {
        super(injector);

        this.primengTableHelper.records = dialogConfig.data['calls'] as CallDetailRecordList[];
        this.primengTableHelper.totalRecordsCount = this.primengTableHelper.records.length ?? 0;
    }

    ngOnInit(): void {
    }

    handleRemove(cdr: CallDetailRecordList) {

    }

    handleSend() {

    }
}
