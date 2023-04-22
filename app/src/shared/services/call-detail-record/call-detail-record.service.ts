import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ConfigService} from "@shared/ultils/config.service";
import {PagedResponse} from "@shared/models";
import {CallDetailRecordList} from "@shared/models/call-detail-record/call-detail-record.model";

@Injectable({providedIn: 'root'})
export class CallDetailRecordService {

    constructor(private _http: HttpClient, private readonly configService: ConfigService) {
    }

    getCallDetailRecords(keyword?: string, fromDate?: number, toDate?: number, page = 1, size = 25) {
        fromDate = !fromDate || fromDate <= 0 ? new Date().getTime() : fromDate;

        const params = {fromDate, page, size};
        if (keyword) params['keyword'] = keyword.trim();
        if (toDate) params['toDate'] = toDate;

        return this._http.get<PagedResponse<CallDetailRecordList>>('http://localhost:8080/api/v1/cdr/', {params});
    }

}