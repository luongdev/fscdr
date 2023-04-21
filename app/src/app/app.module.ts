import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule, Title} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ReactiveFormsModule} from '@angular/forms';

import {
    PERFECT_SCROLLBAR_CONFIG,
    PerfectScrollbarConfigInterface,
    PerfectScrollbarModule,
} from 'ngx-perfect-scrollbar';

import {
    AvatarModule,
    BadgeModule,
    BreadcrumbModule,
    ButtonGroupModule,
    ButtonModule,
    CardModule,
    DropdownModule,
    FooterModule,
    FormModule,
    GridModule,
    HeaderModule,
    ListGroupModule,
    NavModule,
    ProgressModule,
    SharedModule,
    SidebarModule,
    TabsModule,
    UtilitiesModule,
} from '@coreui/angular';

// Import routing module
import {AppRoutingModule} from './app-routing.module';

// Import app component
import {AppComponent} from './app.component';

// Import containers
import {
    DefaultFooterComponent,
    DefaultHeaderComponent,
    DefaultLayoutComponent,
} from './containers';

import {IconModule, IconSetService} from '@coreui/icons-angular';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';
import {ErrorInterceptor, JwtInterceptor} from '@shared/interceptors';
import {ConfigService} from '@shared/ultils/config.service';
import {PagesModule} from './views/pages/pages.module';
import {ToastModule} from "primeng/toast";
import {MessageService} from "primeng/api";
import { DialogModule } from 'primeng/dialog';
import {DialogService} from "primeng/dynamicdialog";

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
    suppressScrollX: true,
};

const APP_CONTAINERS = [
    DefaultFooterComponent,
    DefaultHeaderComponent,
    DefaultLayoutComponent,
];

@NgModule({
    declarations: [AppComponent, ...APP_CONTAINERS],
    imports: [
        HttpClientModule,
        PagesModule,
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        AvatarModule,
        BreadcrumbModule,
        FooterModule,
        DropdownModule,
        GridModule,
        HeaderModule,
        SidebarModule,
        IconModule,
        PerfectScrollbarModule,
        NavModule,
        ButtonModule,
        FormModule,
        UtilitiesModule,
        ButtonGroupModule,
        ReactiveFormsModule,
        SidebarModule,
        SharedModule,
        TabsModule,
        ListGroupModule,
        ProgressModule,
        BadgeModule,
        ListGroupModule,
        CardModule,
        ToastModule,
        DialogModule
    ],
    providers: [
        MessageService,
        DialogService,
        {
            provide: LocationStrategy,
            useClass: HashLocationStrategy,
        },
        {
            provide: APP_INITIALIZER,
            useFactory: (appConfigService: ConfigService) => {
                return () => {
                    return appConfigService.loadAppConfig();
                };
            },
            deps: [ConfigService],
            multi: true,
        },
        {
            provide: PERFECT_SCROLLBAR_CONFIG,
            useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG,
        },
        {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
        {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
        IconSetService,
        Title,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
