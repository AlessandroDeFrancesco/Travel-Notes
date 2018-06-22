//
//  JournalID.m
//  Travel Notes
//
//  Created by gianpaolo on 07/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "JournalID.h"

// Manca la parte serializzabile da implementare

@implementation JournalID

@synthesize owner_id,name;

-(JournalID*) init{
    self = [super init];
    
    if (self){
        NSLog(@"JournalID Created");
    }
    
    return self;
}

- (JournalID*) initWithName : (NSString*) newName andOwnerId: (long long) newOwnerId{
    self = [super init];
    if (self){
        name = newName;
        owner_id = newOwnerId;
    }
    
    NSLog(@"JournalID Created %lld, %@", owner_id, name);
    return self;
}

- (NSString*) toString{
    return [NSString stringWithFormat:@"ID(%@, %lli)", name, owner_id];
}

- (id)copyWithZone:(NSZone *)zone
{
    id copy = [[[self class] alloc] init];
    
    if (copy) {
        [copy setName:self.name];
        [copy setOwner_id:self.owner_id];
    }
    
    return copy;
}

- (BOOL)isEqual:(id)other {
    if (other == self)
        return YES;
    if (!other || ![other isKindOfClass:[self class]])
        return NO;
    
    return [self isEqualToJournalID: other];
}

- (BOOL)isEqualToJournalID:(JournalID *) other{
    if (self == other)
        return YES;
    if (![(id)[self name] isEqual:[other name]])
        return NO;
    if (!([self owner_id] == [other owner_id]))
        return NO;
    return YES;
}

- (NSUInteger)hash {
    NSUInteger result = [[NSNumber numberWithDouble: [self owner_id]] hash];
    result = 31 * result + [name hash];
    
    return result;
}

@end
