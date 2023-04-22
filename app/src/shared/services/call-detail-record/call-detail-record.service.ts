import {EventEmitter, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ConfigService} from "@shared/ultils/config.service";
import {PagedResponse} from "@shared/models";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";

@Injectable({providedIn: 'root'})
export class CallDetailRecordService {

    constructor(private _http: HttpClient, private readonly configService: ConfigService) {
    }

    private readonly store = new Map<string, CallDetailRecordList>();

    private readonly eventEmitter = new EventEmitter<{ event: ('save' | 'delete'), data: CallDetailRecordList }>();

    on(event: ('save' | 'delete'), cb: (data: CallDetailRecordList) => void) {
        this.eventEmitter.subscribe(value => {
            if (!value?.event) return;

            if (value.event === event) cb(value.data);
        });
    }

    get(key: string) {
        return this.store.get(key);
    }

    get keys() {
        return this.store.keys();
    }

    save(cdr: CallDetailRecordList) {
        this.store.set(cdr.id, cdr);

        this.eventEmitter.emit({event: 'save', data: cdr});
    }

    get size() {
        return this.store.size;
    }

    delete(key: string) {
        const data = this.store.get(key);
        if (data) {
            this.store.delete(key);
            this.eventEmitter.emit({event: 'delete', data});
        }
    }

    clear() {
        this.store.clear();
    }


    getCallDetailRecords(keyword?: string, fromDate?: number, toDate?: number, page = 1, size = 25) {
        fromDate = !fromDate || fromDate <= 0 ? new Date().getTime() : fromDate;

        const params = {fromDate, page, size};
        if (keyword) params['keyword'] = keyword.trim();
        if (toDate) params['toDate'] = toDate;

        return this._http.get<PagedResponse<CallDetailRecordList>>('http://localhost:8080/api/v1/cdr/', {params});
    }

    sendCallDetailRecords(findDate: number, ids: string[]) {
        return this._http.post<{ success: boolean, errors: [], data: [] }>(
            'http://localhost:8080/api/v1/cdr/send', {cdrIds: ids}, {params: {findDate}});
    }

}