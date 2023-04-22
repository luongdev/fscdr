export class BaseResponse<T> {
    success: boolean;
    message: string;
    data?: T | undefined | null;
}

export class PagedResponse<T> {
    totalPages: number;
    totalRecords: number;
    data: Array<T>;
}