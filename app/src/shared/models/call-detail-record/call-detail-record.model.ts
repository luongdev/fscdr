export interface CallDetailRecordList {

    id: string;
    globalCallId: string;
    phoneNumber: string;
    dialedNumber: string;
    startTime: number;
    endTime: number;
    direction: string;
    selected?: boolean;
}