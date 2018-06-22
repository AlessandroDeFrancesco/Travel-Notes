//
//  DayView.m
//  Travel Notes
//
//  Created by gianpaolo on 05/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "DayView.h"

@implementation DayView

-(void)fillWithDate:(NSDate *)date{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    
    dateFormatter.dateFormat=@"dd";
    NSString *dayNumber = [dateFormatter stringFromDate:date];
    _day.text = dayNumber;
    
    dateFormatter.dateFormat=@"EEEE";
    NSString *dayString = [[dateFormatter stringFromDate:date] capitalizedString];
    _dayName.text = dayString;

    
    dateFormatter.dateFormat=@"MMMM, YYYY";
    NSString *monthAndYearString = [[dateFormatter stringFromDate:date] capitalizedString];
    _monthAndYear.text = monthAndYearString;
}

-(void)hideLeftArrow:(BOOL) b{
    [_leftArrow setHidden:b];
}

-(void)hideRightArrow:(BOOL) b{
    [_rightArrow setHidden:b];
}

@end
