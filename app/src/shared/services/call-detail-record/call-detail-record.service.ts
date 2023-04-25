import {EventEmitter, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ConfigService} from "@shared/ultils/config.service";
import {PagedResponse} from "@shared/models";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";

@Injectable({providedIn: 'root'})
export class CallDetailRecordService {

    private readonly baseApiUrl: string;

    constructor(private _http: HttpClient, configService: ConfigService) {
        this.baseApiUrl = configService.baseApiUrl ?? '';
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

    has(key: string) {
        return this.store.has(key);
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

        return this._http.get<PagedResponse<CallDetailRecordList>>(`${ this.baseApiUrl}/api/v1/cdr/`, {params});
    }

    sendCallDetailRecords(cdrs: { id: string; startTime: number }[]) {
        return this._http.post<{ success: boolean, data: { successIds: [], errorIds: [] } }>(`${ this.baseApiUrl}/api/v1/cdr/send`, {cdrs});
    }

    changeDomain(cdr: { id: string, domainName: string; startTime: number }) {
        return this._http.post<{ success: boolean }>(`${ this.baseApiUrl}/api/v1/cdr/change-domain`, cdr);
    }

}