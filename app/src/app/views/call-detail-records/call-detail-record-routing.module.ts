import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CallDetailRecordTableComponent} from "./call-detail-record-table/call-detail-record-table.component";

const routes: Routes = [
    {
        path: '',
        component: CallDetailRecordTableComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class CallDetailRecordRoutingModule {
}
