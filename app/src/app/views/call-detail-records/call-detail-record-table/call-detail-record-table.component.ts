import {Component, Injector, OnInit, ViewChild} from '@angular/core';
import {ComponentBase} from "@shared/ultils/component-base.component";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";
import {DialogService} from "primeng/dynamicdialog";
import {CallDetailRecordPreviewComponent} from "../call-detail-record-preview/call-detail-record-preview.component";
import * as moment from 'moment';
import {NzDatePickerComponent} from 'ng-zorro-antd/date-picker';
import {CallDetailRecordService} from "@shared/services/call-detail-record/call-detail-record.service";
import {Paginator} from "primeng/paginator";
import {data} from "autoprefixer";

@Component({
    selector: 'app-call-detail-record-table',
    templateUrl: './call-detail-record-table.component.html',
})
export class CallDetailRecordTableComponent extends ComponentBase<CallDetailRecordList> implements OnInit {

    private readonly startDay = {h: 0, m: 0, s: 0, ms: 0};
    private readonly endDay = {h: 23, m: 59, s: 59, ms: 59};
    private readonly startMonth = {date: 1, ...this.startDay};

    @ViewChild('datePicker') datePicker: NzDatePickerComponent;
    @ViewChild('paginator') paginator: Paginator;

    constructor(
        injector: Injector,
        private readonly _dialogService: DialogService,
        private readonly _callDetailRecordService: CallDetailRecordService) {
        super(injector);
    }

    mode: any = 'date';
    searchValue: string;
    searchDate = new Date();

    ngOnInit(): void {
        this.searchDate = moment(new Date()).set(this.startDay).toDate();
        this._callDetailRecordService.on('delete', data => delete data['selected']);

        this.handleSearch();
    }

    paginate($event: any) {
        this.handleSearch();
    }

    handleClick(cdr: CallDetailRecordList) {
        cdr.selected = true;

        this._callDetailRecordService.save(cdr);
    }

    handlePreviewClick() {
        const dialog = this._dialogService.open(CallDetailRecordPreviewComponent, {
            closeOnEscape: true,
            closable: true,
            autoZIndex: true,
            header: 'Danh sách gửi',
            width: '80vw',
            height: '80vh',
            contentStyle: {'overflow': 'auto'}
        });

        dialog.onClose.subscribe(() => {

        });
    }

    handleResetClick() {
        for (const key of this._callDetailRecordService.keys) {
            delete this._callDetailRecordService.get(key)['selected'];
        }

        this._callDetailRecordService.clear();
    }

    nzDisableDate = (d: Date): boolean => {
        return d > new Date();
    }

    handleSearch() {
        const times = this.times();
        const currentPage = this.primengTableHelper.getCurrentPage(this.paginator);
        const pageSize = this.primengTableHelper.getMaxResultCount(this.paginator, null);

        this.primengTableHelper.showLoadingIndicator();
        this._callDetailRecordService.getCallDetailRecords(
            this.searchValue,
            times[0],
            times[1],
            currentPage, pageSize)
            .subscribe({
                next: v => {
                    this.primengTableHelper.records = v.data;
                    this.primengTableHelper.totalRecordsCount = v.totalRecords;
                },
                complete: () => {
                    this.primengTableHelper.hideLoadingIndicator();
                },
                error: err => {
                    this.primengTableHelper.hideLoadingIndicator();
                }
            })
    }

    get selectCount(): number {
        return this._callDetailRecordService.size;
    }

    private times(): number[] {
        const times = [];
        const selectMoment = moment(this.searchDate ?? new Date());
        if (this.mode === 'date') {
            times[0] = selectMoment.unix() * 1000;
            times[1] = selectMoment.set(this.endDay).unix() * 1000;
        } else {
            times[0] = selectMoment.set(this.startMonth).unix() * 1000;
        }

        return times;
    }
}
