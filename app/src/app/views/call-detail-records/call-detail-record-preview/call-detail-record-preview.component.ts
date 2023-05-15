import {Component, Injector, OnInit} from '@angular/core';
import {ComponentBase} from "@shared/ultils/component-base.component";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {CallDetailRecordService} from "@shared/services/call-detail-record/call-detail-record.service";
import {MessageService} from "primeng/api";

type EditableCallDetailRecordList = CallDetailRecordList & {
    directionEditing?: boolean;
    domainEditing?: boolean;
};

@Component({
    selector: 'app-call-detail-record-preview',
    templateUrl: './call-detail-record-preview.component.html',
})
export class CallDetailRecordPreviewComponent extends ComponentBase<EditableCallDetailRecordList> implements OnInit {

    calls: EditableCallDetailRecordList[] = [];

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
        delete cdr['domainEditing'];
        delete cdr['directionEditing'];

        this._callDetailRecordService.delete(cdr.id);

        this.mapData();
    }

    get isSending(): boolean {
        return this.primengTableHelper.isLoading;
    }

    handleSend() {
        if (this.callsCount <= 0) return;

        this.primengTableHelper.showLoadingIndicator();
        this._callDetailRecordService
            .sendCallDetailRecords(this.requestBody())
            .subscribe({
                next: value => {
                    if (!value?.success) {
                        this.msgService.add({
                            severity: 'error',
                            summary: 'Không thể gửi CDR(s)'
                        })
                        return;
                    }

                    if (value.data) {
                        console.log(value.data);
                        for (const key of value.data.successIds) {
                            this._callDetailRecordService.delete(key);
                        }
                    }

                    this.dialogRef.close(value.data.errorIds);
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

    private requestBody() {
        const cdrs = [];

        for (const key of this._callDetailRecordService.keys) {
            const cdr = this._callDetailRecordService.get(key);
            cdrs.push({id: cdr.id, startTime: cdr.startTime});
        }

        return cdrs;
    }

    private mapData() {
        this.calls = [];
        for (const key of this._callDetailRecordService.keys) {
            this.calls.push(this._callDetailRecordService.get(key));
        }

        this.primengTableHelper.records = this.calls;
        this.primengTableHelper.totalRecordsCount = this.calls.length;
    }

    handleDomainUpdate(cdr: any) {
        console.log(cdr)
    }


    updateDomain(cdr: EditableCallDetailRecordList) {
        cdr.domainEditing = false;

        this.primengTableHelper.showLoadingIndicator();
        this._callDetailRecordService
            .changeDomain({id: cdr.id, domainName: cdr.domainName, startTime: cdr.startTime})
            .subscribe({
                next: value => {
                    if (!value?.success) {
                        this.msgService.add({
                            severity: 'error',
                            summary: `Không thể update domain!`
                        })
                    } else {
                        this.msgService.add({
                            severity: 'success',
                            summary: `Đã update domain sang ${cdr.domainName}!`
                        })
                    }
                },
                error: err => {
                    console.error(`Cannot update domain. Error: `, err?.message);
                    this.primengTableHelper.hideLoadingIndicator();
                },
                complete: () => {
                    this.primengTableHelper.hideLoadingIndicator();
                }
            });
    }

    updateDirection(cdr: EditableCallDetailRecordList) {
        cdr.directionEditing = false;
        this.primengTableHelper.showLoadingIndicator();
        this._callDetailRecordService
            .changeDirection({id: cdr.id, direction: cdr.direction, startTime: cdr.startTime})
            .subscribe({
                next: value => {
                    if (!value?.success) {
                        this.msgService.add({
                            severity: 'error',
                            summary: `Không thể update direction!`
                        })
                    } else {
                        this.msgService.add({
                            severity: 'success',
                            summary: `Đã update direction sang ${cdr.direction}!`
                        })
                    }
                },
                error: err => {
                    console.error(`Cannot update direction. Error: `, err?.message);
                    this.primengTableHelper.hideLoadingIndicator();
                },
                complete: () => {
                    this.primengTableHelper.hideLoadingIndicator();
                }
            });
    }
}
