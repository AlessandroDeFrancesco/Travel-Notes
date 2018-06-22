//
//  ServerCalls.h
//  prova rest
//
//  Created by gianpaolo on 09/10/16.
//  Copyright © 2016 caprara. All rights reserved.
//

#ifndef ServerCalls_h
#define ServerCalls_h

#import <Foundation/Foundation.h>
#import "Element.h"
#import "Journal.h"


@interface ServerCalls : NSObject {
    @public
    void (^_getJournalsCallback)(NSMutableArray* journalsList);
    void (^_getElementCallback)(Element* element);
    void (^_addElementCallback)(bool b);
    void (^_addTokenCallback)(bool b);
    void (^_getJournalCallback)(Journal* journal);
    void (^_newJournalCallback)(bool b);
    void (^_updateJournalCallback)(bool b);
    @private
    NSString* urlBaseString;
    NSString* resourcePath;
}

+ (ServerCalls*) getInstance;
- (void) downloadJournal: (void(^)(Journal*))journalCallback andJournalOwnerID : (long long) journalOwnerID andJournalName : (NSString*) journalName;
- (void) downloadAllJournals: (void(^)(NSMutableArray*))journalsCallback andJournalOwnerID : (long long) journalOwnerID;
- (void) downloadElement: (void(^)(Element*))elementCallback andJournalOwnerID :(long long) journalOwnerID andJournalName : (NSString*) journalName andElementPosition : (NSInteger*) element_position;
-(void) uploadElementToJournal: (void(^)(bool))addElementCallback andNewElement : (Element*) newElement andJournalOwnerID: (long long) journalOwnerID andJournalName : (NSString*) journalName andElementOwnerID : (NSString*) elementOwnerID;
- (void) uploadNewJournal : (void(^)(bool))newJournalCallback andNewJournal : (Journal*) newJournal;
- (void) updateJournal : (void(^)(bool))updateJournalCallback andNewJournal: (Journal*) newJournal;
- (void) addToken : (void(^)(bool))addTokenCallback andIdFacebook : (NSString*) idFacebook andToken : (NSString*) idToken;
@end

#endif /* ServerCalls_h */
