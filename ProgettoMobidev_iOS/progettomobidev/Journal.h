//
//  Journal.h
//  Travel Notes
//
//  Created by gianpaolo on 06/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#ifndef Journal_h
#define Journal_h

#import <Foundation/Foundation.h>
#import "Element.h"
#import "JournalID.h"

@interface Journal : NSObject{
    NSString* name;
    NSString* description;
    NSString* type;
    NSString* city;
    long long owner_id;
    NSDate* departureDate;
    NSDate* returnDate;
    NSMutableArray* participants;
    NSMutableArray* elements;
}

@property (nonatomic,strong) NSString* name;
@property (nonatomic,strong) NSString* description;
@property (nonatomic,strong) NSString* type;
@property (nonatomic,strong) NSString* city;
@property (nonatomic) long long owner_id;
@property (nonatomic,strong) NSDate* departureDate;
@property (nonatomic,strong) NSDate* returnDate;
@property (nonatomic,strong) NSMutableArray* participants;
@property (nonatomic,strong) NSMutableArray* elements;


- (Journal*) initWithName : (NSString*) newName andOwnerId: (long long) newOwnerId;
- (Journal*) initWithName : (NSString*) newName andDescription: (NSString*) newDescription andType: (NSString*) newType andCity: (NSString*) newCity andOwnerId: (long long) newOwnerId;
- (Journal*) initWithName : (NSString*) newName andDescription: (NSString*) newDescription andType: (NSString*) newType andCity: (NSString*) newCity andOwnerId: (long long) newOwnerId andDepartureDate: (NSDate*) newDepartureDate andReturnDate: (NSDate*) newReturnDate;
- (void) addElement: (Element*) element;
- (void) addParticipant: (NSNumber*) participant;
- (Element*) getElement: (NSUInteger) i;
- (NSUInteger) getSize;
- (void) printAll;
- (NSMutableArray<NSDate *>*) getAllJournalDays;
- (NSMutableArray*) getElementsOfDay: (NSDate*) date;
- (NSDate*) getLastDate;
- (JournalID*) getJournalID;

@end
#endif /* Journal_h */
