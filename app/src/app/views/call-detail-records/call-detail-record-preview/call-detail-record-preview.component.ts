import {Component, Injector, OnInit} from '@angular/core';
import {ComponentBase} from "@shared/ultils/component-base.component";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {CallDetailRecordService} from "@shared/services/call-detail-record/call-detail-record.service";
import {MessageService} from "primeng/api";

@Component({
    selector: 'app-call-detail-record-preview',
    templateUrl: './call-detail-record-preview.component.html',
})
export class CallDetailRecordPreviewComponent extends ComponentBase<CallDetailRecordList> implements OnInit {

    readonly callIds: string[] = [];
    readonly calls: CallDetailRecordList[] = [];

    private readonly findDate: number;

    constructor(
        injector: Injector,
        dialogConfig: DynamicDialogConfig,
        private readonly msgService: MessageService,
        private readonly dialogRef: DynamicDialogRef,
        private readonly _callDetailRecordService: CallDetailRecordService) {
        super(injector);

        this.findDate = dialogConfig.data['findDate'];
    }

    get callsCount(): number {
        return this._callDetailRecordService.size;
    }

    ngOnInit(): void {
        this.mapData();
    }

    handleRemove(cdr: CallDetailRecordList) {
        this._callDetailRecordService.delete(cdr.id);

        this.mapData();
    }

    handleSend() {
        if (this.callsCount <= 0) return;

        this.primengTableHelper.showLoadingIndicator();
        this._callDetailRecordService
            .sendCallDetailRecords(this.findDate, this.callIds)
            .subscribe({
                next: value => {
                    if (!value?.success) {
                        this.msgService.add({
                            severity: 'error',
                            summary: 'Không thể gửi CDR(s)'
                        })
                        return;
                    }

                    debugger
                    if (value.data) {
                        console.log(value.data);
                        for (const key of value.data) {
                            this._callDetailRecordService.delete(key);
                        }
                    }

                    this.dialogRef.close(value.errors);
                },
                error: err => {
                    this.primengTableHelper.hideLoadingIndicator();
                    console.error('Cannot send cdr ', err);

                    this.msgService.add({
                        severity: 'error',
                        summary: `Không thể gửi CDR(s)`
                    })
                    return;
                },
                complete: () => {
                    this.primengTableHelper.hideLoadingIndicator();
                }
            });
    }

    private mapData() {
        this.calls.length = 0;
        this.callIds.length = 0;

        for (const key of this._callDetailRecordService.keys) {
            this.calls.push(this._callDetailRecordService.get(key));
            this.callIds.push(key);
        }

        this.primengTableHelper.records = this.calls;
        this.primengTableHelper.totalRecordsCount = this.calls.length;
    }
}
