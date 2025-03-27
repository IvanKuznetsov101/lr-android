//package com.vsu.test.domain.usecase
//
//import com.vsu.test.data.repository.VisitorRepository
//import com.vsu.test.data.storage.VisitorStorage
//import com.vsu.test.data.storage.VisitorInfo
//import com.vsu.test.utils.NetworkResult
//import javax.inject.Inject
//
//class UpdateVisitorStorageUseCase @Inject constructor(
//    private val visitorRepository: VisitorRepository,
//    private val visitorStorage: VisitorStorage) {
//    suspend operator fun invoke(profileId: Long) {
//        val visitorIdStorage = visitorStorage.getVisitorInfo()?.visitorId
//        val visitorResponse = visitorRepository.getCurrentVisitorByProfileId(profileId)
//        if (visitorResponse is NetworkResult.Success){
//            if (visitorIdStorage != null){
//                if(visitorIdStorage != visitorResponse.data?.idVisitor){
//                    visitorStorage.clearVisitorInfo()
//                    visitorStorage.saveVisitorInfo(VisitorInfo(
//                        visitorId = visitorResponse.data!!.idVisitor,
//                        lightRoomId = visitorResponse.data.idLightRoom,
//                        profileId = visitorResponse.data.idProfile))
//                }
//            } else {
//                visitorStorage.saveVisitorInfo(VisitorInfo(
//                    visitorId = visitorResponse.data!!.idVisitor,
//                    lightRoomId = visitorResponse.data.idLightRoom,
//                    profileId = visitorResponse.data.idProfile))
//            }
//        }
//    }
//}
//
