//
//  JournalID.h
//  Travel Notes
//
//  Created by gianpaolo on 07/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#ifndef JournalID_h
#define JournalID_h

#import <Foundation/Foundation.h>

@interface JournalID : NSObject <NSCopying>{
    long long owner_id;
    NSString* name;
}

@property (nonatomic,strong) NSString* name;
@property (nonatomic) long long owner_id;

- (JournalID*) initWithName : (NSString*) newName andOwnerId: (long long) newOwnerId;
- (NSString*) toString;

@end
#endif /* JournalID_h */
