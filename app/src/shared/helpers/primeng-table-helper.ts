import {LazyLoadEvent} from 'primeng/api';
import {Paginator} from 'primeng/paginator';

export class PrimengTableHelper<T> {
    predefinedRecordsCountPerPage = [25, 50, 100];
    defaultRecordsCountPerPage = 50;


    totalRecordsCount = 0;

    records: T[];

    isLoading = false;

    showLoadingIndicator(): void {
        setTimeout(() => {
            this.isLoading = true;
        }, 0);
    }

    hideLoadingIndicator(): void {
        setTimeout(() => {
            this.isLoading = false;
        }, 0);
    }

    getMaxResultCount(paginator: Paginator, event: LazyLoadEvent): number {
        if (!paginator) {
            return this.defaultRecordsCountPerPage;
        }
        if (paginator.rows) {
            return paginator.rows;
        }

        if (!event) {
            return 0;
        }

        return event.rows;
    }


    getCurrentPage(paginator: Paginator): number {
        const page = paginator?.currentPage() ?? 0;
        return page > 0 ? page - 1 : 0;
    }
}
