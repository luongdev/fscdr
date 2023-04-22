import {Component, Injector, OnInit, ViewChild} from '@angular/core';
import {ComponentBase} from "@shared/ultils/component-base.component";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";
import {DialogService} from "primeng/dynamicdialog";
import {CallDetailRecordPreviewComponent} from "../call-detail-record-preview/call-detail-record-preview.component";
import * as moment from 'moment';
import {NzDatePickerComponent} from 'ng-zorro-antd/date-picker';

@Component({
    selector: 'app-call-detail-record-table',
    templateUrl: './call-detail-record-table.component.html',
})
export class CallDetailRecordTableComponent extends ComponentBase<CallDetailRecordList> implements OnInit {

    constructor(injector: Injector, private readonly _dialogService: DialogService) {
        super(injector);
    }

    mode: any = 'date';
    searchValue: string;
    searchDate: Date;
    @ViewChild('datePicker') datePicker: NzDatePickerComponent;

    ngOnInit(): void {
        this.primengTableHelper.records = [
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            }, {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            }, {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            }, {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            },
            {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            }, {
                id: '',
                dialedNumber: '19006969',
                direction: 'inbound',
                globalCallId: '',
                phoneNumber: '0986543211',
                startTime: 98054839,
                endTime: 98054939
            }
        ];

        this.primengTableHelper.totalRecordsCount = 1000;
    }

    paginate($event: any) {

    }

    handleClick(cdr: CallDetailRecordList) {

    }

    handlePreviewClick() {
        const dialog = this._dialogService.open(CallDetailRecordPreviewComponent, {
            closeOnEscape: true,
            closable: true,
            autoZIndex: true,
            header: 'Danh sách gửi',
            width: '80vw',
            height: '80vh',
            contentStyle: {'overflow': 'auto'},
            data: {
                calls: this.primengTableHelper.records
            }
        });

        dialog.onClose.subscribe(() => {

        });
    }

    handleResetClick() {

    }

    nzDisableDate = (d: Date): boolean => {
        return d > new Date();
    }

    handleSearch() {
        console.log(this.searchDate)
        console.log(this.searchValue)
    }
}
