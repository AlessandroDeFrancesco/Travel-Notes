//
//  Journal.m
//  Travel Notes
//
//  Created by gianpaolo on 07/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "Journal.h"

@implementation Journal

// getter e setter
@synthesize name,description,type,city,owner_id,departureDate, returnDate, participants, elements;

// implementazione dei costruttori
- (Journal*) initWithName:(NSString *)newName andOwnerId:(long long)newOwnerId {
    self = [super init];
    if (self){
        name = newName;
        owner_id = newOwnerId;
        description = @"";
        type = @"";
        city = @"";
        departureDate = [NSDate date];
        returnDate = [NSDate date];
        participants = [NSMutableArray array];
        elements = [NSMutableArray array];
    }
    return self;
}

- (Journal*) initWithName : (NSString*) newName andDescription: (NSString*) newDescription andType: (NSString*) newType andCity: (NSString*) newCity andOwnerId: (long long) newOwnerId {
    self = [super init];
    if (self){
        name = newName;
        owner_id = newOwnerId;
        description = newDescription;
        type = newType;
        city = newCity;
        departureDate = [NSDate date];
        returnDate = [NSDate date];
        participants = [NSMutableArray array];
        elements = [NSMutableArray array];
    }
    return self;
}

- (Journal*) initWithName : (NSString*) newName andDescription: (NSString*) newDescription andType: (NSString*) newType andCity: (NSString*) newCity andOwnerId: (long long) newOwnerId andDepartureDate: (NSDate*) newDepartureDate andReturnDate: (NSDate*) newReturnDate {
    self = [super init];
    if (self){
        name = newName;
        owner_id = newOwnerId;
        description = newDescription;
        type = newType;
        city = newCity;
        departureDate =  newDepartureDate;
        returnDate = newReturnDate;
        participants = [NSMutableArray array];
        elements = [NSMutableArray array];
    }
    return self;
}

- (void) addElement: (Element*) element {
    [elements addObject:element];
}

- (void) addParticipant: (NSNumber*) participant{
    [participants addObject: participant];
}

- (Element*) getElement: (NSUInteger) i{
    Element* returnElement = [[Element alloc] init];
    if (i < [self getSize]){
        returnElement = [elements objectAtIndex:i];
    } else {
        returnElement = nil;
    }
    return returnElement;
}

- (NSUInteger) getSize{
    return [elements count];
}

- (void) printAll{
    NSLog(@"%@ \n %llu \n %@ \n %@ \n %@ \n %@ \n %@ \n num elements: %lu \n num participants: %lu" , name, owner_id, type, city, description, departureDate , returnDate, elements.count, participants.count);
}

- (NSMutableArray<NSDate *>*) getAllJournalDays{
    NSMutableSet <NSNumber *> *datesInDays = [[NSMutableSet alloc] init];
    
    for(Element *element in elements){
        NSDateComponents *components = [[NSCalendar currentCalendar] components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear fromDate:element.date];
        NSInteger day = [components day];
        NSInteger month = [components month];
        NSInteger year = [components year];
        
        long calcDate = (year * 100 + month);
        calcDate = calcDate * 100 + day;
        [datesInDays addObject:[NSNumber numberWithLong:calcDate]];
    }
    
    // riconverte in Date
    NSMutableArray<NSDate *> *listOfDays = [[NSMutableArray alloc] init];
    for(NSNumber* d in datesInDays) {
        NSInteger year = d.longValue / 10000;
        NSInteger month = (d.longValue % 10000) / 100;
        NSInteger day = d.longValue % 100;
        
        NSDateComponents *components = [[NSDateComponents alloc] init];
        [components setDay:day];
        [components setMonth:month];
        [components setYear:year];
        NSDate *date = [[NSCalendar currentCalendar] dateFromComponents:components];
        
        [listOfDays addObject: date];
    }
    
    // riordina l'array
    [listOfDays sortUsingSelector:@selector(compare:)];
    
    return listOfDays;
}

- (NSMutableArray*) getElementsOfDay: (NSDate*) date{
    NSMutableArray *list = [NSMutableArray array];
    
    NSDateFormatter *date_formatter = [[NSDateFormatter alloc] init];
    [date_formatter setDateFormat:@"dd/MM/yyyy"];
    NSString *date_string = [date_formatter stringFromDate:date];
    
    for (Element *element in elements){
        NSString *element_date = [date_formatter stringFromDate:element.date];
        if ([date_string isEqualToString:element_date]){
            [list addObject:element];
        }
    }
    return list;
}

- (NSDate*) getLastDate{
    NSDate *lastDate = [[NSDate alloc] init];
    
    if (elements.count == 0){
        lastDate = departureDate;
    } else {
        lastDate = ((Element *)[elements objectAtIndex:0]).date;
        for (Element *element in elements){
            if([lastDate compare:element.date] != NSOrderedAscending){
                lastDate = element.date;
            }
        }
    }
    
    NSLog(@"LAST DATE %@" , lastDate);
    return lastDate;
}

- (JournalID*) getJournalID{
    return [[JournalID alloc] initWithName:name andOwnerId:owner_id];
}
@end
