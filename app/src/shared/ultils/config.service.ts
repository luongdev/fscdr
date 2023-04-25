import {AppConfigModel} from "@shared/models";
import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {environment} from "@env/environment";

@Injectable({
    providedIn: 'root',
})
export class ConfigService {
    private appConfig: AppConfigModel;

    constructor(private http: HttpClient) {
    }

    loadAppConfig() {
        return new Promise((resolve, reject) => {
            this.http
                .get(environment.appConfig)
                .subscribe({
                    next: (value: AppConfigModel) => this.appConfig = value,
                    error: error => {
                        console.error(`Cannot get configuration. Error: `, error);
                        reject(error);
                    },
                    complete: () => resolve(this.appConfig)
                });
        });
    }

    get baseApiUrl(): string {
        return this.appConfig.baseApiUrl;
    }
}