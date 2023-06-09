export interface CallDetailRecordList {

    id: string;
    globalCallId: string;
    phoneNumber: string;
    dialedNumber: string;
    domainName: string;
    startTime: number;
    endTime: number;
    direction: string;
    selected?: boolean;
}